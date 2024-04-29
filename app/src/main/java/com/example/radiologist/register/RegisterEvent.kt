package com.example.radiologist.register

import com.example.radiologist.model.AppUser

sealed interface RegisterEvent {
    data object Idle : RegisterEvent
    data class NavigateToChatBot(val user: AppUser) : RegisterEvent
    data object NavigateToLogin : RegisterEvent

}