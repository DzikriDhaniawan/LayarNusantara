package com.example.layarnusantara.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.layarnusantara.R.drawable.nyi_roro_kidul
import com.example.layarnusantara.R.drawable.hutan_kurcaci
import com.example.layarnusantara.R.drawable.batu_terbelah
import com.example.layarnusantara.component.HeaderView
import com.example.layarnusantara.component.BannerView
import com.example.layarnusantara.component.CategorySection
import com.example.layarnusantara.model.Movie
import com.example.layarnusantara.component.LatestMoviesSection


@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController) {
    val latestMovies = listOf(
        Movie("Legenda Nyi Roro Kidul", nyi_roro_kidul, "Riri", "1h 22m"),
        Movie("Hutan Kurcaci", hutan_kurcaci, "Riri", "1h 22m"),
        Movie("Misteri Batu Terbelah", batu_terbelah, "Riri", "1h 25m")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HeaderView(modifier)
        Spacer(modifier = Modifier.height(10.dp))
        BannerView(modifier = Modifier)
        CategorySection(navController)
        LatestMoviesSection(movies = latestMovies, navController = navController)
    }
}