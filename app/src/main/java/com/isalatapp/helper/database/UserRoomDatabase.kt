package com.isalatapp.helper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.isalatapp.helper.response.UserRecord

/**
 * Created by JokerManX on 6/14/2024.
 */
@Database(entities = [UserRecord::class], version = 1, exportSchema = false)
abstract class UserRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): UserRoomDatabase {
            if (INSTANCE == null) {
                synchronized(UserRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        UserRoomDatabase::class.java, "user_database"
                    )
                        .build()
                }
            }
            return INSTANCE as UserRoomDatabase
        }
    }

}