package com.example.drchat.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Conversation(
var id: String? = null,
): Parcelable {
    companion object {
        const val COLLECTION_NAME = "Rooms"
    }
}

