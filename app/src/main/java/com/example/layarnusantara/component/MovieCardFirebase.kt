package com.example.layarnusantara.component

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.layarnusantara.model.MovieFirebase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun MovieCardFirebase(movie: MovieFirebase, navController: NavController) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    var isBookmarked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        navController.navigate(
                            "movie_detail/${Uri.encode(movie.judul)}/${Uri.encode(movie.thumbnail)}/${Uri.encode(movie.durasi)}/${Uri.encode(movie.synopsis)}/${Uri.encode(movie.video)}"
                        )
                    }
            ) {

                AsyncImage(
                    model = movie.thumbnail,
                    contentDescription = "Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight()
                )


                Column(
                    modifier = Modifier
                        .padding(start = 12.dp, top = 10.dp, bottom = 10.dp, end = 36.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = movie.judul,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2
                    )
                    Text(
                        text = "Durasi: ${movie.durasi}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "⭐ ${movie.review}",
                        fontSize = 12.sp,
                        color = Color(0xFFFFC107),
                        fontWeight = FontWeight.Medium
                    )
                }
            }


            IconButton(
                onClick = {
                    user?.uid?.let { uid ->
                        val data = hashMapOf(
                            "userId" to uid,
                            "judul" to movie.judul,
                            "thumbnail" to movie.thumbnail,
                            "durasi" to movie.durasi,
                            "synopsis" to movie.synopsis,
                            "video" to movie.video,
                            "review" to movie.review
                        )
                        db.collection("favorites").add(data)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                                isBookmarked = true
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Gagal menambahkan", Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (isBookmarked) Color(0xFFFFC107) else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
