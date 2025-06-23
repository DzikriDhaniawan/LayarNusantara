package com.example.layarnusantara.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.layarnusantara.R

@Composable
fun MovieDetailPage(
    navController: NavController,
    title: String,
    thumbnailRes: Int,
    publisher: String,
    duration: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(42.dp))
        // Poster
        Image(
            painter = painterResource(id = thumbnailRes),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Judul
        Text(
            text = title,
            color = Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Info: Tahun, Genre, Durasi, Rating
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "2025 · Action · $duration", color = Color.Gray, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Star, contentDescription = "Rating", tint = Color.Yellow)
                Text(text = "4.8", color = Color.Black, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Deskripsi
        Text(
            text = "Released in 2025, this film follows the story of a former agent on a mission to uncover a global conspiracy. Will he survive?",
            color = Color.Black,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Optional: Continue Watching section
        Text(
            text = "Continue Watching",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            repeat(2) {
                Image(
                    painter = painterResource(id = thumbnailRes),
                    contentDescription = "Continue Watching",
                    modifier = Modifier
                        .size(140.dp)
                        .padding(end = 12.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Optional: Popular section
        Text(
            text = "Popular",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            repeat(3) {
                Image(
                    painter = painterResource(id = thumbnailRes),
                    contentDescription = "Popular",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(end = 12.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

