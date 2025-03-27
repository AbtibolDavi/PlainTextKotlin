package com.example.plaintextkotlin.data.repository

import com.example.plaintextkotlin.data.PasswordDao
import com.example.plaintextkotlin.model.Password
import kotlinx.coroutines.flow.Flow

class DefaultPasswordRepository(private val passwordDao: PasswordDao) : PasswordRepository {

    override fun getPasswords(): Flow<List<Password>> {
        return passwordDao.getAllPasswords()
    }

    override fun searchPasswords(query: String): Flow<List<Password>> {
        return passwordDao.searchPasswords(query)
    }

    override suspend fun getPasswordById(id: Int): Password? {
        return passwordDao.getPasswordById(id)
    }

    override suspend fun insertPassword(password: Password) {
        passwordDao.insertPassword(password)
    }

    override suspend fun updatePassword(password: Password) {
        passwordDao.updatePassword(password)
    }

    override suspend fun deletePassword(password: Password) {
        passwordDao.deletePassword(password)
    }

}