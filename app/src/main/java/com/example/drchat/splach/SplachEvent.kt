package com.example.drchat.splach

import com.example.drchat.model.AppUser

sealed interface SplachEvent {
    data object Idle : SplachEvent
    data object NavigateToLogin : SplachEvent
    data class NavigateToChatBot(val user: AppUser) : SplachEvent
}