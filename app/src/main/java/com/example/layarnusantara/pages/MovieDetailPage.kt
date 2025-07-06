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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material.icons.outlined.BookmarkBorder


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


        val context = LocalContext.current

        var currentReview by remember { mutableStateOf(0f) }
        var isFavorite by remember { mutableStateOf(false) }


        LaunchedEffect(judul) {
            Firebase.firestore.collection("movie")
                .whereEqualTo("judul", judul)
                .get()
                .addOnSuccessListener { result ->
                    for (doc in result) {
                        currentReview = doc.getDouble("review")?.toFloat() ?: 0f
                    }
                }

            Firebase.auth.currentUser?.uid?.let { uid ->
                Firebase.firestore.collection("favorites")
                    .whereEqualTo("userId", uid)
                    .whereEqualTo("judul", judul)
                    .get()
                    .addOnSuccessListener { result ->
                        isFavorite = !result.isEmpty
                    }
            }
        }

        val user = Firebase.auth.currentUser
        val db = Firebase.firestore

        AsyncImage(
            model = thumbnail,
            contentDescription = judul,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = judul,
                    color = Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = durasi,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFC107)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "%.1f".format(currentReview),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        user?.uid?.let { uid ->
                            if (!isFavorite) {
                                val favorite = hashMapOf(
                                    "userId" to uid,
                                    "judul" to judul,
                                    "thumbnail" to thumbnail,
                                    "durasi" to durasi,
                                    "synopsis" to synopsis,
                                    "video" to video
                                )
                                db.collection("favorites")
                                    .add(favorite)
                                    .addOnSuccessListener { isFavorite = true }
                            } else {
                                db.collection("favorites")
                                    .whereEqualTo("userId", uid)
                                    .whereEqualTo("judul", judul)
                                    .get()
                                    .addOnSuccessListener { result ->
                                        for (doc in result) {
                                            db.collection("favorites").document(doc.id).delete()
                                        }
                                        isFavorite = false
                                    }
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFFC107) else Color.Gray
                    )
                }

            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video))
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Tonton Sekarang")
        }


        Spacer(modifier = Modifier.height(10.dp))

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

        Text(
            text = "Komentar & Ulasan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        CommentSection(
            movieId = judul,
            onCommentAdded = {
                refreshRating(judul) { updatedReview ->
                    currentReview = updatedReview
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
    var editMode by remember { mutableStateOf<Comment?>(null) }

    val commentList = remember { mutableStateListOf<Comment>() }

    fun loadComments() {
        db.collection("comments")
            .whereEqualTo("movieId", movieId)
            .get()
            .addOnSuccessListener { result ->
                commentList.clear()
                for (doc in result) {
                    val comment = doc.toObject(Comment::class.java)
                    comment.documentId = doc.id
                    commentList.add(comment)
                }
            }
    }

    LaunchedEffect(movieId) {
        loadComments()
    }

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
                    Text(comment.userName, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(comment.komentar)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("%.1f".format(comment.rating), fontSize = 14.sp)
                    }
                    if (comment.userId == user?.uid) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { editMode = comment }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                                Text("Edit", fontSize = 12.sp)
                            }
                            TextButton(onClick = {
                                deleteComment(comment.documentId) {
                                    loadComments()
                                    onCommentAdded()
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus")
                                Text("Hapus", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

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
                                updateAverageRating(movieId)
                                commentText = ""
                                rating = 3f
                                isSending = false
                                loadComments()
                                onCommentAdded()
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

    if (infoMessage.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(infoMessage, color = Color.Green)
    }

    editMode?.let { comment ->
        var editedText by remember { mutableStateOf(comment.komentar) }
        var editedRating by remember { mutableStateOf(comment.rating) }

        AlertDialog(
            onDismissRequest = { editMode = null },
            confirmButton = {
                TextButton(onClick = {
                    editComment(comment.documentId, editedText, editedRating) {
                        editMode = null
                        loadComments()
                        onCommentAdded()
                    }
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { editMode = null }) {
                    Text("Batal")
                }
            },
            title = { Text("Edit Komentar") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedText,
                        onValueChange = { editedText = it },
                        label = { Text("Komentar") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Rating:")
                    Row {
                        for (i in 1..5) {
                            IconButton(onClick = { editedRating = i.toFloat() }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$i Stars",
                                    tint = if (i <= editedRating.toInt()) Color(0xFFFFC107) else Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        )
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

fun editComment(commentId: String, newText: String, newRating: Float, onSuccess: () -> Unit) {
    Firebase.firestore.collection("comments").document(commentId)
        .update(mapOf(
            "komentar" to newText,
            "rating" to newRating
        ))
        .addOnSuccessListener { onSuccess() }
}

fun deleteComment(commentId: String, onSuccess: () -> Unit) {
    Firebase.firestore.collection("comments").document(commentId)
        .delete()
        .addOnSuccessListener { onSuccess() }
}

fun updateAverageRating(movieId: String) {
    Firebase.firestore.collection("comments")
        .whereEqualTo("movieId", movieId)
        .get()
        .addOnSuccessListener { result ->
            val ratings = result.mapNotNull { it.getDouble("rating")?.toFloat() }
            if (ratings.isNotEmpty()) {
                val avgRating = ratings.average().toFloat()

                Firebase.firestore.collection("movie")
                    .whereEqualTo("judul", movieId)
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


