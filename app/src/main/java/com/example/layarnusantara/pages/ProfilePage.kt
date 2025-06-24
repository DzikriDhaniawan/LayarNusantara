package com.example.layarnusantara.pages

import UserProfile
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*

@Composable
fun ProfilePage(modifier: Modifier) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val userId = auth.currentUser?.uid
    val context = LocalContext.current

    var profile by remember { mutableStateOf(UserProfile()) }
    var isLoading by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf(profile.photoUrl) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    var displayName by remember { mutableStateOf(auth.currentUser?.displayName ?: "") }
    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var job by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val storageRef = storage.reference.child("profile_images/${UUID.randomUUID()}")
                storageRef.putFile(it).addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        imageUrl = downloadUrl.toString()
                        db.collection("users").document(userId!!).update("photoUrl", imageUrl)
                    }
                }
            }
        }

    LaunchedEffect(Unit) {
        userId?.let {
            val snapshot = db.collection("users").document(it).get().await()
            snapshot.toObject(UserProfile::class.java)?.let { user ->
                profile = user
                displayName = user.name
                gender = user.gender
                age = user.age
                job = user.job
                location = user.location
                phone = user.phone
                imageUrl = user.photoUrl
            }
        }
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF1F5F9))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color(0xFF1D3557)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(modifier = Modifier.offset(y = 50.dp)) {
                        Surface(
                            shape = CircleShape,
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { launcher.launch("image/*") },
                            color = Color.White
                        ) {
                            if (!imageUrl.isNullOrEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(imageUrl),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                )
                            }
                        }


                    }
                }

                Spacer(modifier = Modifier.height(60.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = {
                            displayName = it
                            db.collection("users").document(userId!!).update("name", it)
                        },
                        label = { Text("Nama Pengguna") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                } else {
                    Text(
                        text = displayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (!isEditing) {
                            ProfileItem(
                                Icons.Default.Wc,
                                if (gender.isNotBlank()) gender else "Belum diisi"
                            )
                            ProfileItem(
                                Icons.Default.Cake,
                                if (age.isNotBlank()) "$age Tahun" else "Belum diisi"
                            )
                            ProfileItem(
                                Icons.Default.Work,
                                if (job.isNotBlank()) job else "Belum diisi"
                            )
                            ProfileItem(
                                Icons.Default.LocationOn,
                                if (location.isNotBlank()) location else "Belum diisi"
                            )
                            ProfileItem(
                                Icons.Default.Phone,
                                if (phone.isNotBlank()) phone else "Belum diisi"
                            )
                        } else {
                            DropdownField(
                                "Gender",
                                gender,
                                listOf("Laki-Laki", "Wanita"),
                                onSelectedChange = {
                                    gender = it
                                    db.collection("users").document(userId!!).update("gender", it)
                                })
                            NumberPickerField("Umur", age, onValueChange = {
                                age = it
                                db.collection("users").document(userId!!).update("age", it)
                            })
                            ProfileField("Pekerjaan", job) { job = it }
                            ProfileField("Lokasi", location) { location = it }
                            ProfileField("Telepon", phone) { phone = it }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (isEditing) {
                                Button(
                                    onClick = {
                                        isEditing = false
                                        db.collection("users").document(userId!!).update(
                                            mapOf(
                                                "name" to displayName,
                                                "gender" to gender,
                                                "age" to age,
                                                "job" to job,
                                                "location" to location,
                                                "phone" to phone
                                            )
                                        )
                                        val profileUpdate =
                                            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                                .setDisplayName(displayName)
                                                .build()
                                        auth.currentUser?.updateProfile(profileUpdate)

                                        Toast.makeText(
                                            context,
                                            "Profil berhasil diperbarui",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(Color(0xFF1D3557))
                                ) {
                                    Text("Simpan", color = Color.White)
                                }
                            } else {
                                Button(
                                    onClick = { isEditing = true },
                                    colors = ButtonDefaults.buttonColors(Color(0xFF1D3557))
                                ) {
                                    Text("Edit", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // Logout button di pojok kanan atas
            IconButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 16.dp) // MENGGESER TURUN DARI POJOK ATAS
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.Red
                )
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Konfirmasi Logout") },
                    text = { Text("Apakah Anda yakin ingin logout?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showLogoutDialog = false
                            auth.signOut()
                            Toast.makeText(context, "Berhasil logout", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Ya")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileItem(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF1D3557))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp)
    }
}

@Composable
fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
fun DropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelectedChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Text(text = label)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelectedChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NumberPickerField(label: String, value: String, onValueChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = (10..100).map { it.toString() }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Text(text = label)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
