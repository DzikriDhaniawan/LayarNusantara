package com.example.layarnusantara.pages

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.layarnusantara.model.MovieFirebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(modifier: Modifier = Modifier, navController: NavController) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var filmList by remember { mutableStateOf(listOf<Pair<String, MovieFirebase>>()) }
    var filteredList by remember { mutableStateOf(listOf<Pair<String, MovieFirebase>>()) }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("movie")
            .get()
            .addOnSuccessListener { snapshot ->
                val data = snapshot.documents.mapNotNull { doc ->
                    val film = doc.toObject(MovieFirebase::class.java)
                    film?.let { Pair(doc.id, it) }
                }
                filmList = data
                filteredList = data
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(horizontal = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                navController.navigate("home") {
                    popUpTo("search") { inclusive = true }
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    val query = it.text.trim().lowercase()
                    filteredList = if (query.isEmpty()) {
                        filmList
                    } else {
                        filmList.filter { (_, film) ->
                            film.judul.lowercase().contains(query) ||
                                    film.kategori.lowercase().contains(query) ||
                                    film.asal.lowercase().contains(query) ||
                                    film.synopsis.lowercase().contains(query) ||
                                    film.komentar.lowercase().contains(query)
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Cari Judul Film...") },
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Film tidak ditemukan", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredList) { (id, film) ->
                    Text(
                        text = film.judul,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val encodedJudul = Uri.encode(film.judul)
                                val encodedThumbnail = Uri.encode(film.thumbnail)
                                val encodedDurasi = Uri.encode(film.durasi)
                                val encodedSynopsis = Uri.encode(film.synopsis)
                                val encodedVideo = Uri.encode(film.video)

                                navController.navigate(
                                    "movie_detail/$encodedJudul/$encodedThumbnail/$encodedDurasi/$encodedSynopsis/$encodedVideo"
                                )

                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

