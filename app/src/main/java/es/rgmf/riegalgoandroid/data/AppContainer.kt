package es.rgmf.riegalgoandroid.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import es.rgmf.riegalgoandroid.network.ApiService
import es.rgmf.riegalgoandroid.network.BASE_URL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response

class AuthInterceptor(private val tokenFlow: Flow<String>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val token = runBlocking {
            tokenFlow.firstOrNull()
        }

        val newRequest = chain.request().newBuilder()
            .apply {
                if (!token.isNullOrEmpty()) {
                    addHeader("Authorization", "Bearer $token")
                }
            }
            .build()

        return chain.proceed(newRequest)
    }
}

private const val USER_PREFERENCE_NAME = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCE_NAME
)

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val apiRepository: ApiRepository
    val userPreferencesRepository: UserPreferencesRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {
    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     */
    val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * DI implementation for preferences repository
     */
    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(userPreferencesRepository.token))
                .build()
        )
        .build()

    /**
     * Retrofit service object for creating api calls
     */
    private val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    /**
     * DI implementation for RIE Galgo API medias repository
     */
    override val apiRepository: ApiRepository by lazy {
        NetworkApiRepository(retrofitService)
    }
}
