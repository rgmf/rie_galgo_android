package es.rgmf.riegalgoandroid.ui.ephemeris

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import retrofit2.HttpException
import es.rgmf.riegalgoandroid.RieGalgoApplication
import es.rgmf.riegalgoandroid.data.ApiRepository
import es.rgmf.riegalgoandroid.data.UserPreferencesRepository
import es.rgmf.riegalgoandroid.model.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

data class EphemerisData(var medias: List<Media>, var skip: Int, val limit: Int, var endReached: Boolean = false)

sealed interface EphemerisUiState {
    data class Success(val data: EphemerisData) : EphemerisUiState
    data class Error(val message: String) : EphemerisUiState
    data class ErrorAuth(val message: String? = null) : EphemerisUiState
    object Loading : EphemerisUiState
    object LoadingToken: EphemerisUiState
}

class EphemerisViewModel(
    private val apiRepository: ApiRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    var ephemerisUiState: EphemerisUiState by mutableStateOf(EphemerisUiState.LoadingToken)
        private set

    private val ephemerisData = MutableStateFlow(
        EphemerisData(
            medias = listOf(),
            skip = 0,
            limit = 20,
            endReached = false
        )
    )

    init {
        verifyTokenAndFetchEphemeris()
    }

    private fun verifyTokenAndFetchEphemeris() {
        viewModelScope.launch {
            val token = userPreferencesRepository.token.firstOrNull()
            if (token.isNullOrEmpty()) {
                Log.d(TAG, "Token is null or empty")
                ephemerisUiState = EphemerisUiState.ErrorAuth()
            } else {
                ephemerisUiState = EphemerisUiState.Loading
                getEphemeris()
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val tokenObject = apiRepository.login(username, password)
                userPreferencesRepository.setTokenPreference(tokenObject.accessToken)
                getEphemeris()
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    Log.d(TAG, "Error 401: Authentication error")
                    ephemerisUiState = EphemerisUiState.ErrorAuth("Authentication error")
                } else {
                    Log.d(TAG, "Login Error: " + e.message.toString())
                    ephemerisUiState = EphemerisUiState.ErrorAuth("Login error: " + e.message.toString())
                }
            } catch (e: IOException) {
                Log.e(TAG, e.message.toString())
                ephemerisUiState = EphemerisUiState.Error("Login Error: " + e.message.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                ephemerisUiState = EphemerisUiState.Error("Login Error: " + e.message.toString())
            }
        }
    }

    fun getEphemeris() {
        viewModelScope.launch {
            ephemerisUiState = EphemerisUiState.Loading
            ephemerisUiState = try {
                val dataStart = ephemerisData.first()
                val mediaResponse = apiRepository.getEphemeris(dataStart.skip, dataStart.limit)

                ephemerisData.update {
                    it.copy(
                        medias = it.medias + mediaResponse.data,
                        skip = it.skip + it.limit,
                        endReached = mediaResponse.data.size < it.limit
                    )
                }

                val dataEnd = ephemerisData.first()

                EphemerisUiState.Success(dataEnd)
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    Log.d(TAG, "Error 401: Authentication error")
                    EphemerisUiState.ErrorAuth()
                } else {
                    Log.d(TAG, e.message.toString())
                    EphemerisUiState.Error("Error in ephemeris: " + e.message.toString())
                }
            } catch (e: IOException) {
                Log.e(TAG, e.message.toString())
                EphemerisUiState.Error("Error in ephemeris: " + e.message.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                EphemerisUiState.Error("Error in ephemeris: " + e.message.toString())
            }
        }
    }

    companion object {
        private const val TAG = "EphemerisViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as RieGalgoApplication)

                val rieGalgoRepository = application.container.apiRepository
                val userPreferencesRepository = application.container.userPreferencesRepository

                EphemerisViewModel(
                    apiRepository = rieGalgoRepository,
                    userPreferencesRepository = userPreferencesRepository
                )
            }
        }
    }
}