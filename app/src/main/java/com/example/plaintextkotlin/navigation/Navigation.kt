package com.example.plaintextkotlin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.plaintextkotlin.ui.AboutScreen
import com.example.plaintextkotlin.ui.AddPasswordPage
import com.example.plaintextkotlin.ui.LoginPage
import com.example.plaintextkotlin.ui.PasswordDetailPage
import com.example.plaintextkotlin.ui.PasswordPage
import com.example.plaintextkotlin.ui.SettingsScreen

object Routes {
    const val LOGIN = "login"
    const val PASSWORD_PAGE = "password_page/{username}"
    const val ADD_PASSWORD = "add_password"
    const val PASSWORD_DETAILS = "password_details/{passwordId}"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(), startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginPage(navController)
        }

        composable(
            route = Routes.PASSWORD_PAGE,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: "Usuário"
            PasswordPage(navController = navController, username = username)
        }

        composable(Routes.ADD_PASSWORD) {
            AddPasswordPage(navController)
        }

        composable(
            Routes.PASSWORD_DETAILS,
            arguments = listOf(navArgument("passwordId") { type = NavType.IntType })
        ) { backStackEntry ->
            val passwordId = backStackEntry.arguments?.getInt("passwordId") ?: -1
            PasswordDetailPage(navController = navController, passwordId = passwordId)
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController)
        }

        composable(Routes.ABOUT) {
            AboutScreen(navController = navController)
        }
    }
}