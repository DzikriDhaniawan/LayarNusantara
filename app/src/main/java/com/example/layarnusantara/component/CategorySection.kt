package com.example.layarnusantara.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategorySection() {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(text = "Category", modifier = Modifier.padding(bottom = 8.dp))

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp)
        ) {
            CategoryItem(icon = Icons.Default.Movie, label = "Drama")
            CategoryItem(icon = Icons.Default.SentimentVerySatisfied, label = "Comedy")
            CategoryItem(icon = Icons.Default.Favorite, label = "Romance")
            CategoryItem(icon = Icons.Default.Visibility, label = "Horror")
            CategoryItem(icon = Icons.Default.AutoAwesome, label = "Fantasy")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
