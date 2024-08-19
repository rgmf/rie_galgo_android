package es.rgmf.riegalgoandroid.ui

import android.util.Log
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface ApiUiState {
    data class Success(val medias: List<Media>) : ApiUiState
    data class Error(val message: String) : ApiUiState
    data class ErrorAuth(val message: String? = null) : ApiUiState
    object Loading : ApiUiState
    object LoadingToken: ApiUiState
}

class ApiViewModel(
    private val apiRepository: ApiRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var apiUiState: ApiUiState by mutableStateOf(ApiUiState.LoadingToken)
        private set

    /**
     * Call getEphemeris() on init so we can display status immediately.
     */
    init {
        verifyTokenAndFetchEphemeris()
    }

    private fun verifyTokenAndFetchEphemeris() {
        viewModelScope.launch {
            val token = userPreferencesRepository.token.firstOrNull()
            if (token.isNullOrEmpty()) {
                Log.d(TAG, "Token is null or empty")
                apiUiState = ApiUiState.ErrorAuth()
            } else {
                apiUiState = ApiUiState.Loading
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
                    apiUiState = ApiUiState.ErrorAuth("Authentication error")
                } else {
                    Log.d(TAG, "Login Error: " + e.message.toString())
                    apiUiState = ApiUiState.ErrorAuth("Login error: " + e.message.toString())
                }
            } catch (e: IOException) {
                Log.e(TAG, e.message.toString())
                apiUiState = ApiUiState.Error("Login Error: " + e.message.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                apiUiState = ApiUiState.Error("Login Error: " + e.message.toString())
            }
        }
    }

    fun getEphemeris() {
        viewModelScope.launch {
            apiUiState = ApiUiState.Loading
            apiUiState = try {
                val mediaResponse = apiRepository.getEphemeris()
                ApiUiState.Success(mediaResponse.data)
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    Log.d(TAG, "Error 401: Authentication error")
                    ApiUiState.ErrorAuth()
                } else {
                    Log.d(TAG, e.message.toString())
                    ApiUiState.Error("Error in ephemeris: " + e.message.toString())
                }
            } catch (e: IOException) {
                Log.e(TAG, e.message.toString())
                ApiUiState.Error("Error in ephemeris: " + e.message.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                ApiUiState.Error("Error in ephemeris: " + e.message.toString())
            }
        }
    }

    /**
     * Factory for [ApiViewModel] that takes [ApiRepository] as a dependency
     */
    companion object {
        private const val TAG = "ApiViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as RieGalgoApplication)

                val rieGalgoRepository = application.container.apiRepository
                val userPreferencesRepository = application.container.userPreferencesRepository

                ApiViewModel(
                    apiRepository = rieGalgoRepository,
                    userPreferencesRepository = userPreferencesRepository
                )
            }
        }
    }
}