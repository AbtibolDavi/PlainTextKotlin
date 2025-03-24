package com.example.plaintextkotlin.data.repository

import com.example.plaintextkotlin.model.Password

interface PasswordRepository {
    fun getPasswords(): List<Password>
    fun searchPasswords(query: String): List<Password>
}