package com.isalatapp.api.di

import android.content.Context
import com.isalatapp.api.retrofit.ApiConfig
import com.isalatapp.helper.UserPreferences
import com.isalatapp.helper.dataStore
import com.isalatapp.helper.database.UserRoomDatabase
import com.isalatapp.helper.repository.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        val database = UserRoomDatabase.getDatabase(context)
        return UserRepository.getInstance(apiService, pref, database)
    }
}