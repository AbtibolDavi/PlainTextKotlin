package com.example.plaintextkotlin.di

import android.content.Context
import androidx.room.Room
import com.example.plaintextkotlin.data.AppDatabase
import com.example.plaintextkotlin.data.PasswordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "password_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePasswordDao(appDatabase: AppDatabase): PasswordDao {
        return appDatabase.passwordDao()
    }
}