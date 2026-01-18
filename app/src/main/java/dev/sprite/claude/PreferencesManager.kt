package dev.sprite.claude

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        private val SPRITE_NAME_KEY = stringPreferencesKey("sprite_name")
        private val TOKEN_KEY = stringPreferencesKey("token")
    }

    val spriteNameFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[SPRITE_NAME_KEY]
        }

    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY]
        }

    suspend fun saveSpriteName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[SPRITE_NAME_KEY] = name
        }
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun getSpriteName(): String? {
        var name: String? = null
        context.dataStore.data.collect { preferences ->
            name = preferences[SPRITE_NAME_KEY]
            return@collect
        }
        return name
    }

    suspend fun getToken(): String? {
        var token: String? = null
        context.dataStore.data.collect { preferences ->
            token = preferences[TOKEN_KEY]
            return@collect
        }
        return token
    }
}
