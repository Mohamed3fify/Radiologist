package com.example.drchat.register

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drchat.database.FirebaseUtils
import com.example.drchat.model.AppUser
import com.example.drchat.model.DataUtils
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    val firstNameState = mutableStateOf("")
    val firstNameErrorState = mutableStateOf<String?>(null)
    val emailState = mutableStateOf("")
    val emailErrorState = mutableStateOf<String?>(null)
    val passwordState = mutableStateOf("")
    val passwordErrorState = mutableStateOf<String?>(null)
    val confirmPasswordState = mutableStateOf("")
    val confirmPasswordErrorState = mutableStateOf<String?>(null)
    private val auth = Firebase.auth
    val isLoading = mutableStateOf(false)
    val events = mutableStateOf<RegisterEvent>(RegisterEvent.Idle)

    private val _accountAlreadyExists = MutableStateFlow(false)
    val accountAlreadyExists: StateFlow<Boolean> = _accountAlreadyExists


    fun register() {
        if (validateFields()) {
            isLoading.value = true
            auth.createUserWithEmailAndPassword(emailState.value, passwordState.value)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        isLoading.value = false
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            setAccountAlreadyExists(true)
                        } else {
                            Log.e("TAG", "register: ${task.exception?.message}")
                        }

                        return@addOnCompleteListener
                    }
                    val uid = task.result.user?.uid
                    // Add user to cloud Fire store
                    addUserToFireStore(uid)
                }
        }

    }

    private fun addUserToFireStore(uid: String?) {

        val user = AppUser(uid, firstNameState.value, emailState.value)
        FirebaseUtils.addUser(user, onSuccessListener = {
            isLoading.value = false
            DataUtils.appUser = user
            // events.value = RegisterEvent.NavigateToChatBot(user)  , // to navigate to chat bot after create the account
            events.value =
                RegisterEvent.NavigateToLogin   // , to navigate to login after create the account
        }) {
            isLoading.value = false
            Log.e("Tag", "addUserToFirestore: ${it.message}")
        }
    }

    private fun validateFields(): Boolean {

        if (firstNameState.value.isEmpty() && firstNameState.value.isBlank()) {
            firstNameErrorState.value = "Required"
            return false
        } else {
            firstNameErrorState.value = null
        }

        if (emailState.value.isEmpty() && emailState.value.isBlank()) {
            emailErrorState.value = "Required"
            return false
        }
            else if (!Patterns.EMAIL_ADDRESS.matcher(emailState.value).matches()) {
                emailErrorState.value = "Email is badly formatted"
                return false
            }
         else {
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
        //new
        if (confirmPasswordState.value != passwordState.value) {
            confirmPasswordErrorState.value = "Passwords do not match"
            return false
        } else {
            confirmPasswordErrorState.value = null
        }

        return true

    }

    fun navigateToLogin() {
        events.value = RegisterEvent.NavigateToLogin

    }

    fun resetEvent() {
        events.value = RegisterEvent.Idle
    }

    fun setAccountAlreadyExists(value: Boolean) {
        viewModelScope.launch {
            _accountAlreadyExists.value = value
        }
    }

    fun resetAccountAlreadyExists() {
        viewModelScope.launch {
            _accountAlreadyExists.value = false
        }
    }


}