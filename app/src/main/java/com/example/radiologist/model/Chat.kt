package com.example.radiologist.model

import android.graphics.Bitmap
import com.example.radiologist.model.Constants
import com.example.radiologist.model.Conversation
import com.google.firebase.firestore.PropertyName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Chat(
    val prompt: String,
    var bitmap: Bitmap? = null,
    var isFromUser: Boolean,
    var conversationId : String? = null,
    var dateTime: Long? = null,
    var bitmapUri: String? = null,

    ){

     constructor() : this("" ,null , true , "" ,0L , "")
        companion object {
            const val COLLECTION_NAME = "Chat"
        }
    fun formatDateTime(): String {
        val date = Date(dateTime!!)
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return simpleDateFormat.format(date)

    }
}
