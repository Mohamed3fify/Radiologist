package com.example.radiologist.data

import android.graphics.Bitmap
import com.example.radiologist.model.DataUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Chat(
    val prompt: String,
    val bitmap: Bitmap? = null,
    val isFromUser: Boolean,
    var conversationId : String? = null,
    var dateTime: Long? = null,
){
    fun formatDateTime(): String {
        val date = Date(dateTime!!)
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return simpleDateFormat.format(date)

    }
        companion object {
            const val COLLECTION_NAME = "Chat"
        }
}
