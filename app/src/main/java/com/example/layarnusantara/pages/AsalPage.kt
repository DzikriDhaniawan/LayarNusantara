package com.example.layarnusantara.pages

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.layarnusantara.component.MovieCardFirebase
import com.example.layarnusantara.model.MovieFirebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun AsalPage(modifier: Modifier = Modifier, navController: NavController) {
    val movieList = remember { mutableStateListOf<MovieFirebase>() }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("movie")
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
        modifier = modifier.padding(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(movieList) { movie ->
            MovieCardFirebase(movie, navController)
        }
    }

    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("movie")
            .get()
            .addOnSuccessListener { result ->
                movieList.clear()
                for (doc in result) {
                    val movie = doc.toObject(MovieFirebase::class.java)
                    movieList.add(movie)
                }
                isLoading.value = false
            }
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.padding(8.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(movieList) { movie ->
                MovieCardFirebase(movie, navController)
            }
        }
    }

}
