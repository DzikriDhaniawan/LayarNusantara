package com.example.layarnusantara.component

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.layarnusantara.model.MovieFirebase

@Composable
fun MovieCardFirebase(movie: MovieFirebase, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(end = 12.dp)
            .width(250.dp)
            .clickable {
                navController.navigate(
                    "movie_detail/${
                        Uri.encode(movie.judul)
                    }/${
                        Uri.encode(movie.thumbnail)
                    }/${
                        Uri.encode(movie.durasi)
                    }/${
                        Uri.encode(movie.synopsis)
                    }/${
                        Uri.encode(movie.video)
                    }"
                )
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = movie.thumbnail,
                contentDescription = movie.judul,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = movie.judul,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
