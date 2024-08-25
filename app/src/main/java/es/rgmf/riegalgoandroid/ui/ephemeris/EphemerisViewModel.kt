package es.rgmf.riegalgoandroid.ui.ephemeris

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import es.rgmf.riegalgoandroid.R
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
    @StringRes val error: Int? = null,
    val skip: Int = 0,
    val limit: Int = 20,
    val authError: Boolean = false,
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
                uiState.update {
                    it.copy(
                        medias = emptyList(),
                        error = R.string.error_token,
                        isLoading = false,
                        authError = true
                    )
                }
            } else {
                uiState.update { it.copy(error = null, isLoading = true) }
                fetchEphemeris()
            }
        }
    }

    fun fetchEphemeris() {
        viewModelScope.launch {
            uiState.update { it.copy(error = null, isLoading = true) }
            try {
                val mediaResponse = apiRepository.getEphemeris(
                    skip = uiState.value.skip,
                    limit = uiState.value.limit
                )
                val newMedias = mediaResponse.data.filterNot { media ->
                    uiState.value.medias.any { it.id == media.id }
                }

                uiState.update {
                    it.copy(
                        medias = it.medias + newMedias,
                        isLoading = false,
                        error = null,
                        skip = it.skip + it.limit,
                        endReached = mediaResponse.data.size < it.limit
                    )
                }
            } catch (e: HttpException) {
                handleHttpException(e)
            } catch (e: IOException) {
                handleException(R.string.error_network_ephemeris, e)
            } catch (e: Exception) {
                handleException(R.string.error_unexpected_ephemeris, e)
            }
        }
    }

    private fun handleHttpException(e: HttpException) {
        uiState.update {
            if (e.code() == 401) {
                Log.d(TAG, "Error 401: Authentication error: " + e.message())
                it.copy(
                    medias = emptyList(),
                    error = R.string.error_authentication,
                    isLoading = false,
                    authError = true
                )
            } else {
                Log.d(TAG, e.message())
                it.copy(
                    error = R.string.error_unexpected_ephemeris,
                    isLoading = false
                )
            }
        }
    }

    private fun handleException(@StringRes message: Int, e: Exception) {
        Log.e(TAG, e.message.toString())
        uiState.update {
            it.copy(
                error = message,
                isLoading = false
            )
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