package com.example.layarnusantara.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.layarnusantara.R
import com.example.layarnusantara.component.MovieCard
import com.example.layarnusantara.model.Movie

@Composable
fun AsalPage(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Judul Halaman
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0xFF1D3557)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Asal Film",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Bali
        Text(text = "Bali", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D3557))
        Spacer(modifier = Modifier.height(8.dp))
        MovieGridAsal(navController)

        Spacer(modifier = Modifier.height(24.dp))

        // Section Jawa
        Text(text = "Jawa", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D3557))
        Spacer(modifier = Modifier.height(8.dp))
        MovieGridAsal(navController)
    }
}

@Composable
fun MovieGridAsal(navController: NavController) {
    // Dummy Movie List sesuai model lama
    val dummyMovies = listOf(
        Movie(title = "Nyi Roro Kidul", thumbnailRes = R.drawable.nyi_roro_kidul, publisher = "Publisher A", duration = "1h 22m"),
        Movie(title = "Batu Terbelah", thumbnailRes = R.drawable.batu_terbelah, publisher = "Publisher A", duration = "1h 22m"),
        Movie(title = "Hutan Kurcaci", thumbnailRes = R.drawable.hutan_kurcaci, publisher = "Publisher A", duration = "1h 22m"),
        Movie(title = "Batu Terbelah 2", thumbnailRes = R.drawable.batu_terbelah, publisher = "Publisher A", duration = "1h 22m"),
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(dummyMovies) { movie ->
            MovieCard(movie = movie, navController = navController)
        }
    }
}
