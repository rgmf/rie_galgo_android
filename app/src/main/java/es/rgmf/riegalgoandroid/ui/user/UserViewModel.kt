package es.rgmf.riegalgoandroid.ui.user

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
import es.rgmf.riegalgoandroid.model.User
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface UserUiState {
    data class Success(val user: User) : UserUiState
    data class Error(val message: String) : UserUiState
    data class ErrorAuth(val message: String? = null) : UserUiState
    object Loading : UserUiState
    object LoadingToken: UserUiState
}

class UserViewModel(
    private val apiRepository: ApiRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    var userUiState: UserUiState by mutableStateOf(UserUiState.LoadingToken)
        private set

    init {
        verifyTokenAndFetchUser()
    }

    private fun verifyTokenAndFetchUser() {
        viewModelScope.launch {
            val token = userPreferencesRepository.token.firstOrNull()
            if (token.isNullOrEmpty()) {
                Log.d(TAG, "Token is null or empty")
                userUiState = UserUiState.ErrorAuth()
            } else {
                userUiState = UserUiState.Loading
                getUser()
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val tokenObject = apiRepository.login(username, password)
                userPreferencesRepository.setTokenPreference(tokenObject.accessToken)
                getUser()
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    Log.d(TAG, "Error 401: Authentication error")
                    userUiState = UserUiState.ErrorAuth("Authentication error")
                } else {
                    Log.d(TAG, "Login Error: " + e.message.toString())
                    userUiState = UserUiState.ErrorAuth("Login error: " + e.message.toString())
                }
            } catch (e: IOException) {
                Log.e(TAG, e.message.toString())
                userUiState = UserUiState.Error("Login Error: " + e.message.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                userUiState = UserUiState.Error("Login Error: " + e.message.toString())
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userUiState = UserUiState.Loading
            userUiState = try {
                userPreferencesRepository.deleteTokenPreference()
                UserUiState.ErrorAuth()
            } catch (e: IOException) {
                Log.e(TAG, e.message.toString())
                UserUiState.Error("Logout error: " + e.message.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                UserUiState.Error("Logout error: " + e.message.toString())
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            userUiState = UserUiState.Loading
            userUiState = try {
                val userResponse = apiRepository.getUser()
                UserUiState.Success(userResponse.data)
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    Log.d(TAG, "Error 401: Authentication error")
                    UserUiState.ErrorAuth()
                } else {
                    Log.d(TAG, e.message.toString())
                    UserUiState.Error("Error getting user information: " + e.message.toString())
                }
            } catch (e: IOException) {
                Log.e(TAG, e.message.toString())
                UserUiState.Error("Error getting user information: " + e.message.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                UserUiState.Error("Error getting user information: " + e.message.toString())
            }
        }
    }

    companion object {
        private const val TAG = "UserViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as RieGalgoApplication)

                val rieGalgoRepository = application.container.apiRepository
                val userPreferencesRepository = application.container.userPreferencesRepository

                UserViewModel(
                    apiRepository = rieGalgoRepository,
                    userPreferencesRepository = userPreferencesRepository
                )
            }
        }
    }
}