package es.rgmf.riegalgoandroid

import android.app.Application
import es.rgmf.riegalgoandroid.data.AppContainer
import es.rgmf.riegalgoandroid.data.DefaultAppContainer

class RieGalgoApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}