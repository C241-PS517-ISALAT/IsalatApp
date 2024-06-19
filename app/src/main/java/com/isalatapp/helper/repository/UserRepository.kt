package com.isalatapp.helper.repository

import com.isalatapp.api.retrofit.ApiService
import com.isalatapp.helper.UserPreferences
import com.isalatapp.helper.database.UserRoomDatabase
import com.isalatapp.helper.response.AuthResponse
import com.isalatapp.helper.response.UserRecord
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreferences,
    private val database: UserRoomDatabase
) {
    fun getSession(): Flow<UserRecord> {
        return database.userDao().getUser()
    }

    suspend fun saveSession(user: UserRecord) {
        userPreference.saveToken(user)
        database.userDao().insert(user)
    }

    suspend fun clearSession() {
        database.userDao().delete()
        userPreference.clearToken()
    }

    suspend fun editProfile(user: UserRecord) {
        database.userDao().update(user.email, user.name, user.dob, user.phone)
    }

    suspend fun resetPassword(email: String): AuthResponse {
        return try {
            val resetRequest = UserRecord(email = email)
            apiService.resetPassword(resetRequest)
        } catch (e: Exception) {
            AuthResponse(error = true, message = "Error occurred while resetting password")
        }
    }

    suspend fun login(email: String, password: String): AuthResponse {
        val loginRequest = UserRecord(email = email, password = password)
        return apiService.login(loginRequest)
    }

    suspend fun register(name: String, email: String, password: String): AuthResponse {
        val registerRequest = UserRecord(name = name, email = email, password = password)
        return apiService.register(registerRequest)
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