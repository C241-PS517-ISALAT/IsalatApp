package com.isalatapp.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.isalatapp.helper.response.UserRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveToken(user: UserRecord) {
        dataStore.edit { preferences ->
            preferences[token] = user.token.toString()
            preferences[email] = user.email
            preferences[rememberMe] = user.rememberMe
        }
    }

    fun getToken(): Flow<UserRecord> {
        return dataStore.data.map { preferences ->
            UserRecord(
                token = preferences[token] ?: "",
                email = preferences[email] ?: "",
                rememberMe = preferences[rememberMe] ?: false
            )
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(token)
            preferences.remove(email)
            preferences.remove(rememberMe)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null
        private val email = stringPreferencesKey("email")
        private val token = stringPreferencesKey("token")
        private val rememberMe = booleanPreferencesKey("rememberMe")
        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
