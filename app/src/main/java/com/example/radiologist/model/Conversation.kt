package com.example.radiologist.model

data class Conversation(
var id: String? = null,
val messages: List<Message> = emptyList(),
) {
    companion object {
        const val COLLECTION_NAME = "Rooms"
    }
}

data class Message(
    val conversationId : String? = null,
    val senderId: String? = null,
    val content: String? = null,
    val timestamp: Long = System.currentTimeMillis(),

){
    companion object {
        const val MESSAGE_COLLECTION = "Message"
    }
}

