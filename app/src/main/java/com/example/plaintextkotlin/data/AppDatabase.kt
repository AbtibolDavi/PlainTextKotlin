package com.example.plaintextkotlin.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.plaintextkotlin.model.Password

@Database(entities = [Password::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun passwordDao(): PasswordDao
}