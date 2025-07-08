package com.example.layarnusantara.component

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun HeaderView(modifier: Modifier = Modifier, navController: NavController) {
    var name by remember { mutableStateOf("") }

    // Ambil nama pengguna dari Firestore
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            Firebase.firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    val fullName = snapshot.getString("name") ?: ""
                    name = fullName.split(" ").getOrNull(0) ?: ""
                }
                .addOnFailureListener {
                    Log.e("Firestore", "Gagal ambil nama: ${it.message}")
                }
        }
    }

    // Kartu biru gelap dengan sudut kiri bawah bulat
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 1.dp, vertical = 8.dp),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomEnd = 0.dp,
            bottomStart = 32.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A3A)), // Biru gelap
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Halo, $name",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mau tonton apa hari ini?",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )
            }

            IconButton(
                onClick = { navController.navigate("Search Page") }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }
    }
}
