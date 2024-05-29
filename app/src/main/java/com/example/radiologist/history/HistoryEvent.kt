package com.example.radiologist.history

import com.example.radiologist.model.Conversation


sealed interface HistoryEvent {
    data object Idle : HistoryEvent
    data class NavigateToChatScreen(val conversationId: String) : HistoryEvent
    data class DeleteConversation(val conversationId: String) : HistoryEvent

}