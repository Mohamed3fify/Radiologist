package com.example.drchat.register

import com.example.drchat.model.AppUser

sealed interface RegisterEvent {
    data object Idle : RegisterEvent
    data class NavigateToChatBot(val user: AppUser) : RegisterEvent

}