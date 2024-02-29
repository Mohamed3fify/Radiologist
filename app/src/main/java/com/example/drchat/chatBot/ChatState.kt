package com.example.drchat.chatBot

import android.graphics.Bitmap
import com.example.drchat.data.Chat

 data class ChatState(
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null
) {
     companion object
 }
