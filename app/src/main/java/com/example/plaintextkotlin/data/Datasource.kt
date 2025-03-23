package com.example.plaintextkotlin.data

import com.example.plaintextkotlin.R
import com.example.plaintextkotlin.model.Password

class Datasource() {
    fun loadPasswords(): List<Password> {
        return listOf<Password>(
            Password(R.string.password_title_1, R.string.password_username_1, R.string.password_content_1),
            Password(R.string.password_title_2, R.string.password_username_2, R.string.password_content_2),
            Password(R.string.password_title_3, R.string.password_username_3, R.string.password_content_3),
            Password(R.string.password_title_4, R.string.password_username_4, R.string.password_content_4),
            Password(R.string.password_title_5, R.string.password_username_5, R.string.password_content_5),
            Password(R.string.password_title_6, R.string.password_username_6, R.string.password_content_6),
            Password(R.string.password_title_7, R.string.password_username_7, R.string.password_content_7),
            Password(R.string.password_title_8, R.string.password_username_8, R.string.password_content_8),
        )
    }
}