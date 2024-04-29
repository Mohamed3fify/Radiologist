package com.example.radiologist.splach

import com.example.radiologist.model.AppUser

sealed interface SplachEvent {
    data object Idle : SplachEvent
    data object NavigateToLogin : SplachEvent
    data class NavigateToChatBot(val user: AppUser) : SplachEvent
}