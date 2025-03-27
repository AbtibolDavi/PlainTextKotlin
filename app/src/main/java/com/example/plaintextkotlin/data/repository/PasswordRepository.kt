package com.example.plaintextkotlin.data.repository

import com.example.plaintextkotlin.model.Password
import kotlinx.coroutines.flow.Flow

interface PasswordRepository {
    fun getPasswords(): Flow<List<Password>>
    fun searchPasswords(query: String): Flow<List<Password>>
    suspend fun getPasswordById(id: Int): Password?
    suspend fun insertPassword(password: Password)
    suspend fun updatePassword(password: Password)
    suspend fun deletePassword(password: Password)
}