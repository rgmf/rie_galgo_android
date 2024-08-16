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
import coil.network.HttpException
import es.rgmf.riegalgoandroid.RieGalgoApplication
import es.rgmf.riegalgoandroid.data.ApiRepository
import es.rgmf.riegalgoandroid.data.UserPreferencesRepository
import es.rgmf.riegalgoandroid.model.Media
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface ApiUiState {
    data class Success(val medias: List<Media>) : ApiUiState
    object Error : ApiUiState
    object ErrorAuth : ApiUiState
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
                Log.e("RieGalgoUiState", "Token is null or empty")
                apiUiState = ApiUiState.ErrorAuth
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
                if (e.response.code == 401) {
                    Log.e("RieGalgoUiState", "Error 401: Authentication error")
                    ApiUiState.ErrorAuth
                } else {
                    Log.e("RieGalgoUiState", "Login Error: " + e.message.toString())
                    ApiUiState.ErrorAuth
                }
            } catch (e: IOException) {
                Log.e("RieGalgoUiState", e.message.toString())
                ApiUiState.Error
            } catch (e: Exception) {
                Log.e("RieGalgoUiState", e.toString())
                ApiUiState.Error
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
                if (e.response.code == 401) {
                    Log.e("RieGalgoUiState", "Error 401: Authentication error")
                    ApiUiState.ErrorAuth
                } else {
                    Log.e("RieGalgoUiState", e.message.toString())
                    ApiUiState.Error
                }
            } catch (e: IOException) {
                Log.e("RieGalgoUiState", e.message.toString())
                ApiUiState.Error
            } catch (e: Exception) {
                Log.e("RieGalgoUiState", e.toString())
                ApiUiState.Error
            }
        }
    }

    /**
     * Factory for [ApiViewModel] that takes [ApiRepository] as a dependency
     */
    companion object {
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