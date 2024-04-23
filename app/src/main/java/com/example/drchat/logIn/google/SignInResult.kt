package com.example.drchat.logIn.google

data class SignInResult(
    val data : UserData?,
    val errorMessage : String?
)
data class UserData(
    val userId : String,
    val userName :String?,
    val email : String?,
    val profilePictureUrl : String?
)