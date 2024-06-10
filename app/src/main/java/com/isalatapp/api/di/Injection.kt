package com.isalatapp.api.di

import android.content.Context
import com.isalatapp.helper.repository.UserRepository
import com.isalatapp.api.retrofit.ApiConfig
import com.isalatapp.helper.UserPreferences
import com.isalatapp.helper.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return UserRepository.getInstance(apiService, pref)
    }
}