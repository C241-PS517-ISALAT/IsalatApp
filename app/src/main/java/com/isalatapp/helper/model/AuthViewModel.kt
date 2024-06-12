package com.isalatapp.helper.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isalatapp.api.ResultState
import com.isalatapp.helper.repository.UserRepository
import com.isalatapp.helper.response.AuthResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * Created by JokerManX on 6/9/2024.
 */
class AuthViewModel(private val repository: UserRepository) : ViewModel() {
    private val _responseResult = MutableLiveData<ResultState<AuthResponse>>()
    val responseResult = _responseResult

    fun submitLogin(email: String, password: String) {
        viewModelScope.launch {
            try {
                _responseResult.value = ResultState.Loading
                val response = repository.login(email, password)
                if (response.loginResult?.token?.isNotEmpty()!!) {
                    repository.saveSession(response.loginResult.token)
                    _responseResult.value = ResultState.Success(response)
                }
            } catch (e: HttpException) {
                _responseResult.value = ResultState.Error(e.message.toString())
            }
        }
    }

    fun submitRegister(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _responseResult.value = ResultState.Loading
                val response = repository.register(name, email, password)
                if (!response.error!!) {
                    _responseResult.value = ResultState.Success(response)
                }
            } catch (e: HttpException) {
                _responseResult.value = ResultState.Error(e.message.toString())
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                _responseResult.value = ResultState.Loading
                val response = repository.resetPassword(email)
                if (!response.error!!) {
                    _responseResult.value = ResultState.Success(response)
                }
            } catch (e: HttpException) {
                _responseResult.value = ResultState.Error(e.message.toString())
            }
        }
    }
}
