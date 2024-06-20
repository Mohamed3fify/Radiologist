package com.example.radiologist.gemini

import android.graphics.Bitmap
import com.example.radiologist.model.Chat
import com.example.radiologist.model.Conversation
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiData {
    private const val api_key = "AIzaSyBhG66MrA24TQXjRfEyeeOPYKsDvAIo8rI"

    var conversationId: Conversation? = null

        suspend fun getResponse(
            prompt: String ,
            conversationId : String? =null,
            dateTime: Long? = null
        ): Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro", apiKey = api_key
        )

        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }

            return Chat(

                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false,
                conversationId = conversationId ,
                dateTime = dateTime,
            )

        } catch (e: Exception) {
            return Chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false,
                conversationId = "" ,
                dateTime = null,
            )
        }

    }

    suspend fun getResponseWithImage(
        prompt: String,
        bitmap: Bitmap,
        conversationId : String? =null,
        dateTime: Long? = null
    ): Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro-vision", apiKey = api_key
        )

        try {

            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(inputContent)
            }

            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false,
                conversationId = conversationId ,
                dateTime = dateTime,
            )

        } catch (e: Exception) {
            return Chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false,
                conversationId = conversationId ,
                dateTime = null,
            )
        }

    }

 }