package com.example.layarnusantara.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.auth.ktx.auth
import com.example.layarnusantara.model.UserModel


class AuthViewModel : ViewModel(){

    private val auth = Firebase.auth

    private val firestore = Firebase.firestore

    fun signup(email : String, name : String, password : String, onResult: (Boolean,String?)-> Unit ){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    var userId = it.result?.user?.uid

                    val userModel = UserModel(name,email,userId!!)
                firestore.collection("users").document(userId)
                    .set(userModel)
                    .addOnCompleteListener { dbTask ->
                        if(dbTask.isSuccessful){
                            onResult(true,null)

                        }else{
                            onResult(false, "Something Went Wrong")
                        }
                    }
                }else{
                    onResult(false,it.exception?.localizedMessage)
                }
            }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.localizedMessage)
                }
            }
    }
}