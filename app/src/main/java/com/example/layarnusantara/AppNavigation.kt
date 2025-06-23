package com.example.layarnusantara

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.layarnusantara.pages.MovieDetailPage
import com.example.layarnusantara.screen.AuthScreen
import com.example.layarnusantara.screen.Homescreen
import com.example.layarnusantara.screen.LoginScreen
import com.example.layarnusantara.screen.SignupScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val isLoggedIn = Firebase.auth.currentUser != null
    val firstPage = if (isLoggedIn) "home" else "auth"

    NavHost(navController = navController, startDestination = firstPage) {

        composable("auth") {
            AuthScreen(modifier, navController)
        }

        composable("Login") {
            LoginScreen(modifier, navController)
        }

        composable("signup") {
            SignupScreen(modifier, navController)
        }

        composable("home") {
            Homescreen(modifier, navController)
        }

        composable(
            route = "movie_detail/{title}/{thumbnailRes}/{publisher}/{duration}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("thumbnailRes") { type = NavType.IntType },
                navArgument("publisher") { type = NavType.StringType },
                navArgument("duration") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val thumbnailRes = backStackEntry.arguments?.getInt("thumbnailRes") ?: 0
            val publisher = backStackEntry.arguments?.getString("publisher") ?: ""
            val duration = backStackEntry.arguments?.getString("duration") ?: ""

            MovieDetailPage(
                navController = navController,
                title = title,
                thumbnailRes = thumbnailRes,
                publisher = publisher,
                duration = duration
            )
        }
    }
}
