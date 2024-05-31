package com.example.radiologist.history

sealed interface HistoryEvent {
    data object Idle : HistoryEvent
    data class NavigateToChatScreen(val conversationId: String) : HistoryEvent
    data class DeleteConversation(val conversationId: String) : HistoryEvent


}