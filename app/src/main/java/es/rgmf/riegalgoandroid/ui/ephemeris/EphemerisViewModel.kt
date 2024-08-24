package es.rgmf.riegalgoandroid.ui.ephemeris

import android.util.Log
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

data class EphemerisUiState(
    val medias: List<Media> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = "",
    val skip: Int = 0,
    val limit: Int = 20,
    val endReached: Boolean = false
)

class EphemerisViewModel(
    private val apiRepository: ApiRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    var uiState = MutableStateFlow(EphemerisUiState())

    init {
        verifyTokenAndFetchEphemeris()
    }

    private fun verifyTokenAndFetchEphemeris() {
        viewModelScope.launch {
            val token = userPreferencesRepository.token.firstOrNull()
            if (token.isNullOrEmpty()) {
                Log.d(TAG, "Token is null or empty")
                uiState.update { it.copy(error = "Token is null or empty", isLoading = false) }
            } else {
                uiState.update { it.copy(error = "", isLoading = true) }
                fetchEphemeris()
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val tokenObject = apiRepository.login(username, password)
                userPreferencesRepository.setTokenPreference(tokenObject.accessToken)
                fetchEphemeris()
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    Log.d(TAG, "Error 401: Authentication error")
                    uiState.update { it.copy(error = "Authentication error", isLoading = false) }
                } else {
                    Log.d(TAG, "Login Error: " + e.message.toString())
                    uiState.update {
                        it.copy(error = "Login error: " + e.message.toString(), isLoading = false)
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, e.message.toString())
                uiState.update {
                    it.copy(error = "Login error: " + e.message.toString(), isLoading = false)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                uiState.update {
                    it.copy(error = "Login error: " + e.message.toString(), isLoading = false)
                }
            }
        }
    }

    fun fetchEphemeris() {
        viewModelScope.launch {
            uiState.update { it.copy(error = "", isLoading = true) }
            try {
                val mediaResponse = apiRepository.getEphemeris(
                    skip = uiState.value.skip,
                    limit = uiState.value.limit
                )
                uiState.update {
                    it.copy(
                        medias = it.medias + mediaResponse.data,
                        isLoading = false,
                        error = "",
                        skip = it.skip + it.limit,
                        endReached = mediaResponse.data.size < it.limit
                    )
                }
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    Log.d(TAG, "Error 401: Authentication error")
                    uiState.update { it.copy(error = "Authentication error", isLoading = false) }
                } else {
                    Log.d(TAG, e.message.toString())
                    uiState.update {
                        it.copy(
                            error = "Error in ephemeris: " + e.message.toString(),
                            isLoading = false
                        )
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, e.message.toString())
                uiState.update {
                    it.copy(
                        error = "Error in ephemeris: " + e.message.toString(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                uiState.update {
                    it.copy(
                        error = "Error in ephemeris: " + e.message.toString(),
                        isLoading = false
                    )
                }
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