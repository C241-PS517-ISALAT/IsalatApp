package com.isalatapp.api.retrofit

import com.isalatapp.helper.response.AuthResponse
import com.isalatapp.helper.response.UserRecord
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("users/signin")
    suspend fun login(
        @Body loginRequest: UserRecord
    ): AuthResponse

    @POST("users/signup")
    suspend fun register(
        @Body registerRequest: UserRecord
    ): AuthResponse

    @POST("users/resetpassword")
    suspend fun resetPassword(
        @Body resetPasswordRequest: UserRecord
    ): AuthResponse

    @GET("profile/profile")
    suspend fun getProfile(
        @Body profileRequest: UserRecord
    ): AuthResponse
}