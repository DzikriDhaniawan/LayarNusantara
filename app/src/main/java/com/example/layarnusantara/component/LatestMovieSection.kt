package com.example.layarnusantara.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.layarnusantara.model.Movie

@Composable
fun LatestMoviesSection(movies: List<Movie>, navController: NavController) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = "Terbaru!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(start = 16.dp)
        ) {
            movies.forEach { movie ->
                MovieCard(movie = movie, navController = navController)
            }
        }
    }
}
