package es.rgmf.riegalgoandroid.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        const val TAG = "UserPreferencesRepo"

        val SERVER = stringPreferencesKey("server")
        val USERNAME = stringPreferencesKey("username")
        val PASSWORD = stringPreferencesKey("password")
        val TOKEN = stringPreferencesKey("token")
    }

    val server: Flow<String> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading server preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences -> preferences[SERVER] ?: "" }

    val username: Flow<String> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading username preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences -> preferences[USERNAME] ?: "" }

    val password: Flow<String> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading password preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences -> preferences[PASSWORD] ?: "" }

    val token: Flow<String> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading token preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences -> preferences[TOKEN] ?: "" }

    suspend fun hasToken(): Boolean {
        val token = dataStore.data
            .map { preferences -> preferences[TOKEN] }
            .firstOrNull()
        return !token.isNullOrEmpty()
    }

    suspend fun setServerPreference(server: String) {
        dataStore.edit { preferences ->
            preferences[SERVER] = server
        }
    }

    suspend fun setUsernamePreference(username: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = username
        }
    }

    suspend fun setPasswordPreference(password: String) {
        dataStore.edit { preferences ->
            preferences[PASSWORD] = password
        }
    }

    suspend fun setTokenPreference(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }
}