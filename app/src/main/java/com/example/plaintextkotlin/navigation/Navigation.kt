package com.example.plaintextkotlin.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.plaintextkotlin.data.Datasource
import com.example.plaintextkotlin.ui.AddPasswordPage
import com.example.plaintextkotlin.ui.LoginPage
import com.example.plaintextkotlin.ui.PasswordDetailPage
import com.example.plaintextkotlin.ui.PasswordPage

object Routes {
    const val LOGIN = "login"
    const val PASSWORD_PAGE = "password_page/{username}"
    const val ADD_PASSWORD = "add_password"
    const val PASSWORD_DETAILS = "password_details/{passwordId}"
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginPage(navController)
        }
        composable(
            route = Routes.PASSWORD_PAGE,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        )   { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            PasswordPage(navController = navController, username = username)
        }
        composable(Routes.ADD_PASSWORD) {
            AddPasswordPage(navController)
        }
        composable(
            Routes.PASSWORD_DETAILS,
            arguments = listOf(navArgument("passwordId") { type = NavType.IntType })
            ) { entry ->
            val passwordId = entry.arguments?.getInt("passwordId")
            Log.d("Navigation", "PasswordId recebido como argumento: $passwordId")
            val passwords = Datasource().loadPasswords()
            val password = passwords.find { it.titleResourceId == passwordId }
            Log.d("Navigation", "Senha encontrada para o ID: ${password?.let { stringResource(it.titleResourceId) } ?: "null"}")
            PasswordDetailPage(navController = navController, password = password)
        }
    }
}