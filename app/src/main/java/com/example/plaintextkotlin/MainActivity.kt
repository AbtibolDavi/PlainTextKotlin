package com.example.plaintextkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.plaintextkotlin.navigation.Navigation
import com.example.plaintextkotlin.ui.theme.PlainTextKotlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlainTextKotlinTheme() {
                Navigation()
            }
        }
    }
}
