package com.example.layarnusantara.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.layarnusantara.model.Comment
import com.google.firebase.auth.ktx.auth
import androidx.compose.material.icons.filled.Send


@Composable
fun MovieDetailPage(
    navController: NavController,
    judul: String,
    thumbnail: String,
    durasi: String,
    synopsis: String,
    video: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(42.dp))

        // Poster (pakai AsyncImage karena ini dari URL)
        AsyncImage(
            model = thumbnail,
            contentDescription = judul,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        val context = LocalContext.current

        var currentReview by remember { mutableStateOf(0f) }

        LaunchedEffect(judul) {
            Firebase.firestore.collection("movie")
                .whereEqualTo("judul", judul)
                .get()
                .addOnSuccessListener { result ->
                    for (doc in result) {
                        currentReview = doc.getDouble("review")?.toFloat() ?: 0f
                    }
                }
        }

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video))
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(text = "Tonton Sekarang")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Judul Film
        Text(
            text = judul,
            color = Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Info: Durasi & Rating
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "$durasi", color = Color.Gray, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "%.1f".format(currentReview),
                    color = Color.Gray,
                    fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Synospis
        Text(
            text = "Synopsis",
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = synopsis,
            color = Color.Black,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        val commentList = remember { mutableStateListOf<Comment>() }

        LaunchedEffect(judul) {
            Firebase.firestore.collection("comments")
                .whereEqualTo("movieId", judul)
                .get()
                .addOnSuccessListener { result ->
                    commentList.clear()
                    for (doc in result) {
                        val comment = doc.toObject(Comment::class.java)
                        commentList.add(comment)
                    }
                }
        }
        Text(
            text = "Komentar & Ulasan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (commentList.isEmpty()) {
            Text("Belum ada komentar", color = Color.Gray)
        } else {
            commentList.forEach { comment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = comment.userName,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D3557)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = comment.komentar)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107)
                            )
                            Text(
                                text = "%.1f".format(comment.rating ?: 0f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(24.dp))
        CommentSection(
            movieId = judul,
            onCommentAdded = {
                refreshRating(judul) { updatedReview ->
                    currentReview = updatedReview
                }
                Firebase.firestore.collection("comments")
                    .whereEqualTo("movieId", judul)
                    .get()
                    .addOnSuccessListener { result ->
                        commentList.clear()
                        for (doc in result) {
                            val comment = doc.toObject(Comment::class.java)
                            commentList.add(comment)
                        }
                    }
            }
        )


    }
}

@Composable
fun CommentSection(
    movieId: String,
    onCommentAdded: () -> Unit
) {
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser

    var commentText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(3f) }
    var isSending by remember { mutableStateOf(false) }
    var infoMessage by remember { mutableStateOf("") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text("Rating:", fontSize = 14.sp)
        for (i in 1..5) {
            IconButton(onClick = { rating = i.toFloat() }) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "$i Stars",
                    tint = if (i <= rating.toInt()) Color(0xFFFFC107) else Color.LightGray
                )
            }
        }
    }

    Column {
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            label = { Text("Tulis komentar...") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            isSending = true
                            val comment = Comment(
                                movieId = movieId,
                                userId = user?.uid ?: "anon",
                                userName = user?.displayName ?: "Anonim",
                                komentar = commentText,
                                rating = rating
                            )

                            db.collection("comments")
                                .add(comment)
                                .addOnSuccessListener {
                                    infoMessage = "Komentar berhasil dikirim!"
                                    commentText = ""
                                    rating = 3f
                                    isSending = false
                                    onCommentAdded()
                                }

                            db.collection("comments")
                                .whereEqualTo("movieId", movieId)
                                .get()
                                .addOnSuccessListener { result ->
                                    val ratings = result.mapNotNull { it.getDouble("rating")?.toFloat() }
                                    if (ratings.isNotEmpty()) {
                                        val avgRating = ratings.average().toFloat()

                                        db.collection("movie")
                                            .whereEqualTo("judul", movieId)
                                            .get()
                                            .addOnSuccessListener { docs ->
                                                for (doc in docs) {
                                                    db.collection("movie").document(doc.id)
                                                        .update("review", avgRating)
                                                }
                                            }
                                    }
                                }
                                .addOnFailureListener {
                                    infoMessage = "Gagal mengirim komentar"
                                    isSending = false
                                }
                        }
                    },
                    enabled = !isSending
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Kirim",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            singleLine = true
        )


        Spacer(modifier = Modifier.height(8.dp))



        Spacer(modifier = Modifier.height(8.dp))

        if (infoMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(infoMessage, color = Color.Green)
        }
    }
}

fun refreshRating(judul: String, onUpdated: (Float) -> Unit) {
    Firebase.firestore.collection("comments")
        .whereEqualTo("movieId", judul)
        .get()
        .addOnSuccessListener { result ->
            val ratings = result.mapNotNull { it.getDouble("rating")?.toFloat() }
            if (ratings.isNotEmpty()) {
                val avgRating = ratings.average().toFloat()
                onUpdated(avgRating)

                Firebase.firestore.collection("movie")
                    .whereEqualTo("judul", judul)
                    .get()
                    .addOnSuccessListener { docs ->
                        for (doc in docs) {
                            Firebase.firestore.collection("movie").document(doc.id)
                                .update("review", avgRating)
                        }
                    }
            }
        }
}
