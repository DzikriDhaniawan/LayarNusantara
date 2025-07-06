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
import com.example.layarnusantara.pages.AsalPage
import com.example.layarnusantara.pages.FavoritePage
import com.example.layarnusantara.pages.TemaPage
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
            route = "movie_detail/{judul}/{thumbnail}/{durasi}/{synopsis}/{video}",
            arguments = listOf(
                navArgument("judul") { type = NavType.StringType },
                navArgument("thumbnail") { type = NavType.StringType },
                navArgument("durasi") { type = NavType.StringType },
                navArgument("synopsis") { type = NavType.StringType },
                navArgument("video") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val judul = backStackEntry.arguments?.getString("judul") ?: ""
            val thumbnail = backStackEntry.arguments?.getString("thumbnail") ?: ""
            val durasi = backStackEntry.arguments?.getString("durasi") ?: ""
            val synopsis = backStackEntry.arguments?.getString("synopsis") ?: ""
            val video = backStackEntry.arguments?.getString("video") ?: ""

            MovieDetailPage(
                navController = navController,
                judul = judul,
                thumbnail = thumbnail,
                durasi = durasi,
                synopsis = synopsis,
                video = video
            )
        }

        composable("Asal") {
            AsalPage(modifier, navController)
        }

        composable("Tema") {
            TemaPage(modifier, navController)
        }

        composable("favorite") {
            FavoritePage(modifier, navController)
        }


    }
}
