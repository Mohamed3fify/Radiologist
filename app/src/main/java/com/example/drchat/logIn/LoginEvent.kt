package com.example.drchat.logIn

import android.content.IntentSender
import com.example.drchat.model.AppUser

 sealed interface LoginEvent {
    data object Idle : LoginEvent
    data object NavigateToRegister : LoginEvent
    data class NavigateToChatBot(val user: AppUser) : LoginEvent

    data object LoginFailed : LoginEvent



}