package com.example.layarnusantara.repository

import com.example.layarnusantara.model.MovieFirebase
import com.google.firebase.firestore.FirebaseFirestore

fun fetchMoviesFromFirestore(onResult: (List<MovieFirebase>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("movie")
        .get()
        .addOnSuccessListener { result ->
            val movieList = result.documents.mapNotNull { it.toObject(MovieFirebase::class.java) }
            onResult(movieList)
        }
        .addOnFailureListener {
            onResult(emptyList())
        }
}
