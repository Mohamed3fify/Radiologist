package com.example.drchat.logIn


import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drchat.database.FirebaseUtils
import com.example.drchat.logIn.google.SignInResult
import com.example.drchat.logIn.google.SignInState
import com.example.drchat.model.AppUser
import com.example.drchat.model.DataUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    val emailState = mutableStateOf("")
    val emailErrorState = mutableStateOf<String?>(null)
    val passwordState = mutableStateOf("")
    val passwordErrorState = mutableStateOf<String?>(null)
    val isLoading = mutableStateOf(false)
    val events = mutableStateOf<LoginEvent>(LoginEvent.Idle)
    val auth = Firebase.auth

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess : StateFlow<Boolean> = _loginSuccess

    // Google SignIn
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()



    fun login(){
        if (validateFields()) {
            isLoading.value = true
            auth.signInWithEmailAndPassword(emailState.value, passwordState.value)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        isLoading.value = false
                            events.value = LoginEvent.LoginFailed
                        Log.e("TAG", "error -> ${task.exception?.message}")
                        return@addOnCompleteListener
                    } else setLoginSuccess(true)
                    val uid = task.result.user?.uid
                    // get User from Firestore
                    getUserFromFirestore(uid!!)
                }
        }
    }
    private fun getUserFromFirestore(uid: String) {
        FirebaseUtils.getUser(uid, onSuccessListener = { documentSnapshot ->
            isLoading.value = false
            val user = documentSnapshot.toObject(AppUser::class.java)
            DataUtils.appUser = user
            navigateToChatBot(user!!)
        }, onFailureListener = {
            isLoading.value = false
        })
    }
     fun validateFields(): Boolean {

        if (emailState.value.isEmpty() && emailState.value.isBlank()) {
            emailErrorState.value = "Required"
            return false
        } else {
            emailErrorState.value = null
        }
        if (passwordState.value.isEmpty() && passwordState.value.isBlank()) {
            passwordErrorState.value = "Required"
            return false
        } else {
            passwordErrorState.value = null
        }
        if (passwordState.value.length < 6) {
            passwordErrorState.value = "Password can't be less that 6 Chars or numbers"
            return false
        } else {
            passwordErrorState.value = null
        }

         return true
    }
    fun navigateToRegister() {
        events.value = LoginEvent.NavigateToRegister

    }

    fun navigateToChatBot(user: AppUser) {
        events.value = LoginEvent.NavigateToChatBot(user)
    }

    fun resetEvent() {
        events.value = LoginEvent.Idle
    }


    fun setLoginSuccess(value: Boolean) {
        viewModelScope.launch {
            _loginSuccess.value = value
        }
    }

    fun resetLoginSuccesss() {
        viewModelScope.launch {
            _loginSuccess.value = false
        }
    }

    // Google SignIn
    fun onSignInResult(result : SignInResult){
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }

    fun resetState(){
        _state.update { SignInState() }
    }

}