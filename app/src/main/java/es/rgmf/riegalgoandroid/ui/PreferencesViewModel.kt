package es.rgmf.riegalgoandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import es.rgmf.riegalgoandroid.RieGalgoApplication
import es.rgmf.riegalgoandroid.data.UserPreferencesRepository

class PreferencesViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val tokenFlow = userPreferencesRepository.token

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as RieGalgoApplication)
                val userPreferencesRepository = application.container.userPreferencesRepository
                PreferencesViewModel(
                    userPreferencesRepository = userPreferencesRepository
                )
            }
        }
    }
}