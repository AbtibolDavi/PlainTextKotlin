package com.example.plaintextkotlin.data.repository

import android.content.Context
import com.example.plaintextkotlin.data.Datasource
import com.example.plaintextkotlin.model.Password

class DefaultPasswordRepository(private val context: Context) : PasswordRepository {
    private val datasource = Datasource()

    override fun getPasswords(): List<Password> {
        return datasource.loadPasswords()
    }

    override fun searchPasswords(query: String): List<Password> {
        val allPasswords = datasource.loadPasswords()
        return allPasswords.filter { password ->
            context.getString(password.titleResourceId).contains(query, ignoreCase = true) ||
                    context.getString(password.usernameResourceId).contains(query, ignoreCase = true)
        }
    }
}