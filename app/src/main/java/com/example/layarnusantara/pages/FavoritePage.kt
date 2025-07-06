package com.example.layarnusantara.pages

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
    var favoriteList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(user?.uid) {
        user?.uid?.let { uid ->
            db.collection("favorites")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { result ->
                    favoriteList = result.documents.mapNotNull { it.data }
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
                items(favoriteList) { movie ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val encodedJudul = Uri.encode(movie["judul"] as? String ?: "")
                                val encodedThumbnail = Uri.encode(movie["thumbnail"] as? String ?: "")
                                val encodedDurasi = Uri.encode(movie["durasi"] as? String ?: "")
                                val encodedSynopsis = Uri.encode(movie["synopsis"] as? String ?: "")
                                val encodedVideo = Uri.encode(movie["video"] as? String ?: "")

                                navController.navigate(
                                    "movie_detail/$encodedJudul/$encodedThumbnail/$encodedDurasi/$encodedSynopsis/$encodedVideo"
                                )

                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp),
                        border = BorderStroke(1.dp, Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            AsyncImage(
                                model = movie["thumbnail"] as? String,
                                contentDescription = movie["judul"] as? String,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(MaterialTheme.shapes.medium)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = movie["judul"] as? String ?: "",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = movie["durasi"] as? String ?: "",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
