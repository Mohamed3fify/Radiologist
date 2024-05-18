package com.example.radiologist.chatBot

import android.graphics.Bitmap

sealed class ChatUiEvent {

    data class UpdatePrompt(val newPrompt: String) : ChatUiEvent()
    data class SendPrompt(
        val prompt: String,
        val bitmap: Bitmap?,
        val conversationId : String? =null,
        val dateTime : Long,

    ) : ChatUiEvent()
    data object BotTyping : ChatUiEvent()
    data class ImageClicked(val bitmap: Bitmap) : ChatUiEvent()
}