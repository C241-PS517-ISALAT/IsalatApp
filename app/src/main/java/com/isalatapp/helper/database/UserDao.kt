package com.isalatapp.helper.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.isalatapp.helper.response.UserRecord
import kotlinx.coroutines.flow.Flow

/**
 * Created by JokerManX on 6/14/2024.
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserRecord)

    @Query("UPDATE userprofile SET name = :newName, dob = :newDob, phone = :newPhone WHERE email = :email")
    suspend fun update(email: String, newName: String, newDob: String, newPhone: String)

    @Query("DELETE FROM userProfile")
    suspend fun delete()

    @Query("SELECT * FROM userProfile LIMIT 1")
    fun getUser(): Flow<UserRecord>
}