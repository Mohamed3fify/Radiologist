package com.example.radiologist.logIn

import com.example.radiologist.model.AppUser

 sealed interface LoginEvent {
    data object Idle : LoginEvent
    data object NavigateToRegister : LoginEvent
    data class NavigateToChatBot(val user: AppUser) : LoginEvent

    data object LoginFailed : LoginEvent
    data object LoginFailedEmailNotVerified : LoginEvent



 }