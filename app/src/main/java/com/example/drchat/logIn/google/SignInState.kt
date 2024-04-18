package com.example.drchat.logIn.google

data class SignInState(
    val isSignInSuccessful : Boolean = false,
    val signInError : String? = null,
    val isLoading : Boolean = false
)
