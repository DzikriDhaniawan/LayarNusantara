package com.example.layarnusantara.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.layarnusantara.model.MovieFirebase

@Composable
fun LatestMoviesFromFirebaseSection(
    movies: List<MovieFirebase>,
    navController: NavController
) {
    Column {
        Text("Terbaru!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            items(movies) { movie ->
                MovieCardFirebase(movie = movie, navController = navController)
            }
        }
    }
}

