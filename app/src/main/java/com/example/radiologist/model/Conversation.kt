package com.example.radiologist.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Conversation(
    var id: String? = null,
    val userId:String? = null,
    var name: String = ""
) : Parcelable
{
    companion object {
        const val COLLECTION_NAME = "Conversation"
    }

}

