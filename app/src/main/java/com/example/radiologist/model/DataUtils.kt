package com.example.radiologist.model

import com.example.radiologist.logIn.google.UserData
import com.google.firebase.auth.FirebaseUser

object DataUtils {
    var appUser: AppUser? = null
    var firebaseUser: FirebaseUser? = null
    var conversation : Conversation? =null
    var googleUser : UserData? =null

}