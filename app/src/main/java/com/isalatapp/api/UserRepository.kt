package com.isalatapp.api

import com.isalatapp.api.retrofit.ApiService
import com.isalatapp.api.retrofit.UserPreferences

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreferences,
) {
    fun getSession() = userPreference.getToken()

    suspend fun saveSession(token: String) = userPreference.saveToken(token)

    suspend fun clearSession() {
        userPreference.clearToken()
    }

    suspend fun login(email: String, password: String) = apiService.login(email, password)

    suspend fun register(name: String, email: String, password: String) =
        apiService.register(name, email, password)

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            apiService: ApiService, pref: UserPreferences
        ): UserRepository = instance ?: synchronized(this) {
            instance ?: UserRepository(apiService, pref).also { instance = it }
        }
    }
}