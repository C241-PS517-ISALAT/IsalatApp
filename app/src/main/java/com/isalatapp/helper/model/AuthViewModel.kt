package com.isalatapp.helper.model

import androidx.lifecycle.LiveData
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
    val responseResult: LiveData<ResultState<AuthResponse>> get() = _responseResult

    private val _userRecord = MutableLiveData<UserRecord>()
    val userRecord: LiveData<UserRecord> get() = _userRecord

    init {
        viewModelScope.launch {
            repository.getSession().collect { userRecord ->
                _userRecord.value = userRecord
            }
        }
    }

    fun editProfile(updatedUser: UserRecord): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            try {
                repository.editProfile(updatedUser) // Pass updatedUser to repository
                result.postValue(true) // Post result as true if successful
            } catch (e: Exception) {
                result.postValue(false) // Post result as false if there's an error
            }
        }
        return result
    }

    fun getSession(): LiveData<UserRecord> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearSession()
        }
    }


    fun submitLogin(user: UserRecord, password: String) {
        viewModelScope.launch {
            try {
                _responseResult.value = ResultState.Loading
                val response = repository.login(email = user.email, password = password)
                repository.saveSession(user)
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
