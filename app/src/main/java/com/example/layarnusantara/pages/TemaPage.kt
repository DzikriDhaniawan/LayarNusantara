package com.example.layarnusantara.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@Composable
fun TemaPage(modifier: Modifier = Modifier, navController: NavController) {
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
                text = "Tema Film",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Kisah Rakyat
        Text(text = "Kisah Rakyat", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D3557))
        Spacer(modifier = Modifier.height(8.dp))
        MovieGridTema(navController, kategori = "Kisah Rakyat")

        Spacer(modifier = Modifier.height(24.dp))

        // Section Legenda
        Text(text = "Legenda", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D3557))
        Spacer(modifier = Modifier.height(8.dp))
        MovieGridTema(navController, kategori = "Legenda")
    }
}

@Composable
fun MovieGridTema(navController: NavController, kategori: String) {
    val movieList = remember { mutableStateListOf<MovieFirebase>() }

    // Load data berdasarkan kategori
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
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(movieList) { movie ->
            MovieCardFirebase(movie = movie, navController = navController)
        }
    }
}
