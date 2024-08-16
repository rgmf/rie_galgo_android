package es.rgmf.riegalgoandroid.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import es.rgmf.riegalgoandroid.network.RieGalgoService
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val rieGalgoRepository: RieGalgoRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class DefaultAppContainer : AppContainer {
    private val baseUrl = "http://192.168.1.23:8000/"

    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     */
    val json = Json {
        ignoreUnknownKeys = true
    }
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    /**
     * Retrofit service object for creating api calls
     */
    private val retrofitService: RieGalgoService by lazy {
        retrofit.create(RieGalgoService::class.java)
    }

    /**
     * DI implementation for RIE Galgo API medias repository
     */
    override val rieGalgoRepository: RieGalgoRepository by lazy {
        NetworkRieGalgoRepository(retrofitService)
    }
}
