package com.isalatapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isalatapp.helper.repository.UserRepository
import com.isalatapp.api.di.Injection
import com.isalatapp.helper.UserPreferences
import com.isalatapp.helper.dataStore
import com.isalatapp.helper.model.AuthViewModel

class ViewModelFactory private constructor(
    private val userRepository: UserRepository, private val pref: UserPreferences
) : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(
                Injection.provideRepository(context),
                pref = UserPreferences.getInstance(context.dataStore)
            )
        }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
//            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
//                return MainViewModel(userRepository) as T
//            }
//
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                return AuthViewModel(userRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}