package com.example.layarnusantara.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.layarnusantara.component.HeaderView
import com.example.layarnusantara.component.BannerView
import com.example.layarnusantara.component.CategorySection
import com.example.layarnusantara.component.LatestMoviesFromFirebaseSection
import com.example.layarnusantara.model.MovieFirebase
import com.example.layarnusantara.repository.fetchMoviesFromFirestore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController) {
    var latestMovies by remember { mutableStateOf<List<MovieFirebase>>(emptyList()) }

    LaunchedEffect(true) {
        fetchMoviesFromFirestore {
            latestMovies = it
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HeaderView(modifier = Modifier)
        Spacer(modifier = Modifier.height(10.dp))
        BannerView(modifier = Modifier)
        CategorySection(navController)

        if (latestMovies.isNotEmpty()) {
            LatestMoviesFromFirebaseSection(movies = latestMovies, navController = navController)
        }
    }
}

