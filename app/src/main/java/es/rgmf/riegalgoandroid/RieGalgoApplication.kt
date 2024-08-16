package es.rgmf.riegalgoandroid

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import es.rgmf.riegalgoandroid.data.AppContainer
import es.rgmf.riegalgoandroid.data.DefaultAppContainer
import es.rgmf.riegalgoandroid.data.UserPreferencesRepository

class RieGalgoApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}