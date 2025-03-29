package com.example.plaintextkotlin.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.plaintextkotlin.R


@Entity(tableName = "passwords")
data class Password(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val title: String, val username: String, val content: String,

    @DrawableRes val imageResourceId: Int = R.drawable.item_key_novo
)
