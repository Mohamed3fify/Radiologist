package com.example.drchat.data

import android.graphics.Bitmap

data class Chat(
    val prompt: String,
    val bitmap: Bitmap?, // for image , i will send to bot
    val isFromUser: Boolean
)
