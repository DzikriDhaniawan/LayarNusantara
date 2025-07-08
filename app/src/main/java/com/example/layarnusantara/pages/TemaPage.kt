package com.example.layarnusantara.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.layarnusantara.component.MovieCardFirebase
import com.example.layarnusantara.model.MovieFirebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun TemaPage(modifier: Modifier = Modifier, navController: NavController) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomEnd = 34.dp,
                            bottomStart = 34.dp
                        )
                    )
                    .background(Color(0xFF1D3557)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tema Film",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        val kategoriList = listOf("Cerita Rakyat", "Legenda")

        kategoriList.forEach { kategori ->
            item {
                Text(
                    text = kategori,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D3557),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                MovieGridTema(navController = navController, kategori = kategori)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun MovieGridTema(navController: NavController, kategori: String) {
    val movieList = remember { mutableStateListOf<MovieFirebase>() }

    // Ambil data berdasarkan kategori dari Firestore
    LaunchedEffect(kategori) {
        Firebase.firestore.collection("movie")
            .whereEqualTo("kategori", kategori)
            .get()
            .addOnSuccessListener { result ->
                movieList.clear()
                for (doc in result) {
                    val movie = doc.toObject(MovieFirebase::class.java)
                    movieList.add(movie)
                }
            }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp) // batasi tinggi agar tidak crash
    ) {
        items(movieList) { movie ->
            MovieCardFirebase(
                movie = movie,
                navController = navController
            )
        }
    }
}
