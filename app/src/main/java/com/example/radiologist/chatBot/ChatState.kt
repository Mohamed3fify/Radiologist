package com.example.radiologist.chatBot

import android.graphics.Bitmap
import com.example.radiologist.model.Chat

data class ChatState(
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null,
    val isTyping: Boolean = false,
    val isLoading: Boolean = false,
    )
 {
     companion object
 }
