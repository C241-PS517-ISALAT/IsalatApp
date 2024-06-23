package com.isalatapp.helper.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.isalatapp.R
import com.isalatapp.api.ResultState
import com.isalatapp.helper.repository.UserRepository
import com.isalatapp.helper.response.AuthResponse
import com.isalatapp.helper.response.UserRecord
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

/**
 * Created by JokerManX on 6/9/2024.
 */
class AuthViewModel(private val repository: UserRepository) : ViewModel() {
    private val _responseResult = MutableLiveData<ResultState<AuthResponse>>()
    val responseResult = _responseResult

    private val _userProfile = MutableLiveData<UserRecord>()
    val userProfile = _userProfile

    init {
        viewModelScope.launch {
            fetchProfileData()
            getSession()
        }
    }

    private suspend fun fetchProfileData() {
        try {
            _responseResult.value = ResultState.Loading
            val profile = repository.getProfile()
            profile.user?.let {
                _userProfile.postValue(it)
            }
            _responseResult.value = ResultState.Success(profile)
        } catch (e: Exception) {
            responseResult.value = ResultState.Error(e.message ?: "Unknown error occurred")
        }
    }

    fun updateProfile(updatedProfile: UserRecord) {
        viewModelScope.launch {
            try {
                _responseResult.value = ResultState.Loading
                val response = repository.updateProfile(updatedProfile)
                response.user?.let { userProfile ->
                    _userProfile.postValue(userProfile)
                }
                _responseResult.value = ResultState.Success(response)
            } catch (e: Exception) {
                responseResult.value = ResultState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getSession() = repository.getSession().asLiveData()

    fun logout() {
        viewModelScope.launch {
            repository.clearSession()
        }
    }

    fun submitLogin(user: UserRecord) {
        viewModelScope.launch {
            try {
                _responseResult.value = ResultState.Loading
                val rememberMe = user.rememberMe
                val response = repository.login(user)
                val userWithRememberMe = response.user?.copy(rememberMe = rememberMe)
                repository.saveSession(response.token!!, userWithRememberMe!!)
                _responseResult.value = ResultState.Success(response)
            } catch (e: Exception) {
                _responseResult.value = ResultState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun submitRegister(userRecord: UserRecord) {
        viewModelScope.launch {
            try {
                _responseResult.value = ResultState.Loading
                val response = repository.register(userRecord)
                if (!response.error!!) {
                    _responseResult.value = ResultState.Success(response)
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody!!).getString("error")
                } catch (jsonException: JSONException) {
                    R.string.error.toString()
                }
                _responseResult.value = ResultState.Error(errorMessage)
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                _responseResult.value = ResultState.Loading
                val response = repository.resetPassword(email = email)
                if (response.error == null || response.error == false) {
                    _responseResult.value = ResultState.Success(response)
                } else {
                    _responseResult.value =
                        ResultState.Error("Error occurred while resetting password")
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    errorBody?.let {
                        JSONObject(it).getString("error")
                    } ?: R.string.error.toString()
                } catch (jsonException: JSONException) {
                    R.string.error.toString()
                }
                _responseResult.value = ResultState.Error(errorMessage)
            } catch (e: Exception) {
                _responseResult.value = ResultState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

}
