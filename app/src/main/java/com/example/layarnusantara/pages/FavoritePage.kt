package com.example.layarnusantara.pages

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun FavoritePage(modifier: Modifier = Modifier, navController: NavController) {
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    var favoriteList by remember { mutableStateOf<List<Pair<String, Map<String, Any>>>>(emptyList()) }

    LaunchedEffect(user?.uid) {
        user?.uid?.let { uid ->
            db.collection("favorites")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { result ->
                    favoriteList = result.documents.mapNotNull {
                        val data = it.data
                        if (data != null) Pair(it.id, data) else null
                    }
                }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Film Favorit",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favoriteList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada film favorit", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favoriteList) { (docId, movie) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = movie["thumbnail"] as? String,
                                contentDescription = movie["judul"] as? String,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable {
                                        val encodedJudul = Uri.encode(movie["judul"] as? String ?: "")
                                        val encodedThumbnail = Uri.encode(movie["thumbnail"] as? String ?: "")
                                        val encodedDurasi = Uri.encode(movie["durasi"] as? String ?: "")
                                        val encodedSynopsis = Uri.encode(movie["synopsis"] as? String ?: "")
                                        val encodedVideo = Uri.encode(movie["video"] as? String ?: "")

                                        navController.navigate(
                                            "movie_detail/$encodedJudul/$encodedThumbnail/$encodedDurasi/$encodedSynopsis/$encodedVideo"
                                        )
                                    }
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {
                                Text(
                                    text = movie["judul"] as? String ?: "",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Durasi: ${movie["durasi"] as? String ?: "-"}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val rating = (movie["review"] as? Number)?.toFloat() ?: 0f
                                Text(
                                    text = "⭐ $rating",
                                    fontSize = 12.sp,
                                    color = Color(0xFFFFC107),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            IconButton(onClick = {
                                db.collection("favorites").document(docId).delete().addOnSuccessListener {
                                    favoriteList = favoriteList.filterNot { it.first == docId }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}