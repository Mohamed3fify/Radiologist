package com.example.radiologist.model

interface SharedInterface  {
    data class DeleteCurrentConversation(val conversationId: String) : SharedInterface

}