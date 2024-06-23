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

    suspend fun saveToken(token: String, user: UserRecord) {
        dataStore.edit { preferences ->
            preferences[token_key] = token
            preferences[name] = user.name ?: ""
            preferences[dob] = user.dob ?: ""
            preferences[phone] = user.phone ?: ""
            preferences[email] = user.email
            preferences[rememberMe_key] = user.rememberMe ?: false
        }
    }

    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[token_key]
        }
    }

    fun getSession(): Flow<UserRecord> {
        return dataStore.data.map { preferences ->
            UserRecord(
                email = preferences[email] ?: "",
                name = preferences[name] ?: "",
                phone = preferences[phone] ?: "",
                dob = preferences[dob] ?: "",
                rememberMe = preferences[rememberMe_key] ?: false
            )
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(token_key)
            preferences.remove(email)
            preferences.remove(name)
            preferences.remove(phone)
            preferences.remove(dob)
            preferences.remove(rememberMe_key)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null
        private val email = stringPreferencesKey("email")
        private val dob = stringPreferencesKey("birthday")
        private val name = stringPreferencesKey("username")
        private val phone = stringPreferencesKey("phone")
        private val token_key = stringPreferencesKey("token_key")
        private val rememberMe_key = booleanPreferencesKey("rememberMe")
        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
