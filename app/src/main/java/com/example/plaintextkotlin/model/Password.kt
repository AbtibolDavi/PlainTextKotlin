package com.example.plaintextkotlin.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.plaintextkotlin.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Password(
    @StringRes val titleResourceId: Int,
    @StringRes val usernameResourceId: Int,
    @StringRes val contentResourceId: Int,
    @DrawableRes val imageResourceId: Int = R.drawable.item_key_novo
) : Parcelable
