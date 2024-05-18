package com.example.radiologist.splach

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.radiologist.database.FirebaseUtils
import com.example.radiologist.database.FirebaseUtils.getGoogleSignInUser
import com.example.radiologist.model.AppUser
import com.example.radiologist.model.DataUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashViewModel : ViewModel() {
    val event = mutableStateOf<SplachEvent>(SplachEvent.Idle)
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    init { checkUserAuthentication() }


    fun navigate() {
        if (auth.currentUser != null ) {
            getUserFromFireStore(auth.currentUser?.uid ?: run {
                navigateToLogin()
                return
            })
        } else {
            navigateToLogin()
        }
    }

    private fun getUserFromFireStore(uid: String) {

        val userRef = firestore.collection("users").document(uid)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(AppUser::class.java)
                    if (user != null) {
                        DataUtils.appUser = user
                        if (Firebase.auth.currentUser?.isEmailVerified == true) {
                            navigateToChatBot(user)
                        } else {
                            navigateToLogin()
                        }
                    } else {
                        navigateToLogin()
                    }
                } else {
                    saveUserAndNavigateToChatBot()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "getUserFromFirestore: ${exception.message}")
                navigateToLogin()
            }
    }

    private fun saveUserAndNavigateToChatBot() {
        val currentUser = getGoogleSignInUser()
        if (currentUser != null) {
            val appUser = AppUser(
                uid = currentUser.userId,
                email = currentUser.email,
                displayName = currentUser.userName,
                firstName = currentUser.userName
            )
            val firebaseUser = auth.currentUser
            if (firebaseUser != null && firebaseUser.isEmailVerified) {

                FirebaseUtils.addUser(appUser,
                    {
                        DataUtils.appUser = appUser
                        navigateToChatBot(appUser)
                    },
                    {
                        Log.e("TAG", "Failed to save user to Firestore: ${it.message}")
                        navigateToLogin()
                    }
                )
            } else {
                navigateToLogin()
            }
        } else {
            Log.e("TAG", "Current user is null")
            navigateToLogin()
        }
    }


    private fun checkUserAuthentication() {
        if (auth.currentUser != null ) {
            getUserFromFireStore(auth.currentUser!!.uid)
        } else {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        event.value = SplachEvent.NavigateToLogin
    }

    private fun navigateToChatBot(user: AppUser) {
        event.value = SplachEvent.NavigateToChatBot(user)
    }
}