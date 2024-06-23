package com.isalatapp.helper.repository

import com.isalatapp.api.retrofit.ApiService
import com.isalatapp.helper.UserPreferences
import com.isalatapp.helper.database.UserRoomDatabase
import com.isalatapp.helper.response.AuthResponse
import com.isalatapp.helper.response.UserRecord

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreferences,
    private val database: UserRoomDatabase
) {
    fun getSession() = userPreference.getSession()

    suspend fun getProfile(): AuthResponse {
        return apiService.getProfile()
    }

    suspend fun updateProfile(updatedProfile: UserRecord): AuthResponse {
        val response = apiService.postProfile(updatedProfile)
        saveSession(response.token!!, response.user!!)
        return response
    }

    suspend fun saveSession(token: String, user: UserRecord) {
        userPreference.saveToken(token, user)
        database.userDao().insert(user)
    }

    suspend fun clearSession() {
        database.userDao().delete()
        userPreference.clearToken()
    }

    suspend fun resetPassword(email: String): AuthResponse {
        return try {
            val resetRequest = UserRecord(email = email)
            apiService.resetPassword(resetRequest)
        } catch (e: Exception) {
            AuthResponse(error = true, message = "Error occurred while resetting password")
        }
    }

    suspend fun login(user: UserRecord): AuthResponse {
        val response = apiService.login(user)
        return response
    }

    suspend fun register(userRecord: UserRecord): AuthResponse {
        return apiService.register(userRecord)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            apiService: ApiService, pref: UserPreferences, database: UserRoomDatabase
        ): UserRepository = instance ?: synchronized(this) {
            instance ?: UserRepository(apiService, pref, database).also { instance = it }
        }
    }
}