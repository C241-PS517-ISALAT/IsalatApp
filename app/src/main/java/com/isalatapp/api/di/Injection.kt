package com.isalatapp.api.di

import android.content.Context
import com.isalatapp.api.UserRepository
import com.isalatapp.api.retrofit.ApiConfig
import com.isalatapp.api.retrofit.UserPreferences
import com.isalatapp.api.retrofit.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return UserRepository.getInstance(apiService, pref)
    }
}