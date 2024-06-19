package com.isalatapp.helper.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @field:SerializedName("error") val error: Boolean? = null,

    @field:SerializedName("message") val message: String? = null,

    @field:SerializedName("user") val user: UserRecord? = null,
)

@Entity(tableName = "userProfile")
data class UserRecord(
    @field:SerializedName("name") val name: String = "",

    @field:SerializedName("phone") val phone: String = "",

    @field:SerializedName("dob") val dob: String = "",

    @PrimaryKey
    @field:SerializedName("email") val email: String = "",

    @field:SerializedName("password") val password: String? = null,

    @field:SerializedName("token") val token: String? = null,

    @field:SerializedName("rememberMe") val rememberMe: Boolean = false
)