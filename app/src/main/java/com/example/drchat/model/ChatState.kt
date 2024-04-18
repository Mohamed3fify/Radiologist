package com.example.drchat.model

import android.graphics.Bitmap
import com.example.drchat.data.Chat

 data class ChatState(
     val chatList: MutableList<Chat> = mutableListOf(),
     val prompt: String = "",
     val bitmap: Bitmap? = null,
     val isTyping: Boolean = false,
     val userId: String? = null,
     val userName: String? = null,
     val conversationId: String? = null,


) {
     companion object {
         const val COLLECTION_NAME = "Messages"
     }
 }
