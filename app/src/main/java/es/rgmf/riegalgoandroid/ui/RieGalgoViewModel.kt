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
import es.rgmf.riegalgoandroid.data.RieGalgoRepository
import es.rgmf.riegalgoandroid.model.Media
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface RieGalgoUiState {
    data class Success(val medias: List<Media>, val token: String) : RieGalgoUiState
    object Error : RieGalgoUiState
    object Loading : RieGalgoUiState
}

class RieGalgoViewModel(private val rieGalgoRepository: RieGalgoRepository) : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var rieGalgoUiState: RieGalgoUiState by mutableStateOf(RieGalgoUiState.Loading)
        private set

    /**
     * Call getEphemeris() on init so we can display status immediately.
     */
    init {
        getEphemeris()
    }

    fun getEphemeris() {
        viewModelScope.launch {
            rieGalgoUiState = RieGalgoUiState.Loading
            rieGalgoUiState = try {
                val token = rieGalgoRepository.login(username = "", password = "")
                val mediaResponse = rieGalgoRepository.getEphemeris(token.accessToken)
                RieGalgoUiState.Success(mediaResponse.data, token.accessToken)
            } catch (e: IOException) {
                Log.e("RieGalgoUiState", e.message.toString())
                RieGalgoUiState.Error
            } catch (e: HttpException) {
                Log.e("RieGalgoUiState", e.message.toString())
                RieGalgoUiState.Error
            } catch (e: Exception) {
                Log.e("RieGalgoUiState", e.toString())
                RieGalgoUiState.Error
            }
        }
    }

    /**
     * Factory for [RieGalgoViewModel] that takes [RieGalgoRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as RieGalgoApplication)
                val rieGalgoRepository = application.container.rieGalgoRepository
                RieGalgoViewModel(rieGalgoRepository = rieGalgoRepository)
            }
        }
    }
}