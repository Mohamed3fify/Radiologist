package com.example.radiologist.chatBot

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiologist.data.Chat
import com.example.radiologist.data.ChatData
import com.example.radiologist.database.FirebaseUtils
import com.example.radiologist.database.FirebaseUtils.saveChatMessage
import com.example.radiologist.model.ChatState
import com.example.radiologist.model.Conversation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class ChatViewModel : ViewModel() {

    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    private val _clickedImage = MutableStateFlow<Bitmap?>(null)

    var conversation: Conversation? = null

    var chat: Chat? = null

    private var _currentConversationId: String? = null
    val currentConversationId: String?
        get() = _currentConversationId

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.SendPrompt -> {
                if (event.prompt.isNotEmpty()) {
                    _chatState.update {
                        it.copy(isTyping = true)
                    }
                    val conversationId = event.conversationId
                    val dateTime = event.dateTime

                    //addPrompt(event.prompt, event.bitmap, event.dateTime)
                    onSendPrompt(event.prompt, event.bitmap, conversationId!!, dateTime)
                    viewModelScope.launch {
                        try {
                            if (event.bitmap != null) {
                                getResponseWithImgFromModel(event)
                            } else {
                                getResponseFromModel(event)
                            }
                        } catch (e: Exception) {
                            // Handle the exception
                        } finally {
                            onEvent(ChatUiEvent.BotTyping)
                        }
                    }
                }
            }

            is ChatUiEvent.BotTyping -> {
                _chatState.update {
                    it.copy(isTyping = false)
                }
            }

            is ChatUiEvent.UpdatePrompt -> {
                _chatState.update {
                    it.copy(prompt = event.newPrompt)
                }
            }

            is ChatUiEvent.ImageClicked -> {
                _clickedImage.update { event.bitmap }
            }

        }
    }

    fun setCurrentConversationId(id: String) {
        _currentConversationId = id
    }

    private fun addPrompt(prompt: String, bitmap: Bitmap?, dateTime: Long) {

        val chat = Chat(
            prompt,
            bitmap,
            true,
            conversation?.id,
            Date().time
        )

        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, chat)
                },
                prompt = "",
                bitmap = null
            )
        }
        saveChatMessage(chat, _currentConversationId!!)
    }

    private suspend fun getResponseWithImgFromModel(event: ChatUiEvent.SendPrompt) {
        val response = ChatData
            .getResponseWithImage(
                event.prompt,
                event.bitmap!!,
                event.conversationId!!
            )
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, response)
                }
            )
        }
        saveChatMessage(response, _currentConversationId!!)
    }

    private suspend fun getResponseFromModel(event: ChatUiEvent.SendPrompt) {
        val response = ChatData.getResponse(
            event.prompt,
            event.conversationId!!
        )
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, response)
                }
            )
        }
        saveChatMessage(response, _currentConversationId!!)
    }

    private fun sendMessage(
        prompt: String,
        bitmap: Bitmap?,
        conversationId: String,
        currentTime: Long
    ) {
        addPrompt(prompt, bitmap, currentTime)

    }

    private fun addConversation(name: String, callback: (String) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return

        val conversation = Conversation(
            name = name,
            id = UUID.randomUUID().toString(),
            userId = userId,
        )
        FirebaseUtils.saveConversation(
            conversation,
            onSuccessListener = {
                _currentConversationId = conversation.id
                callback(conversation.id!!)

            }, onFailureListener = {
                Log.e("TAG", "addConversation: ${it.message}")
            })

    }

    private fun onSendPrompt(
        prompt: String,
        bitmap: Bitmap?,
        conversationId: String,
        currentTime: Long
    ) {
        if (_currentConversationId == null) {
            addConversation(prompt) { newConversationId ->

                sendMessage(prompt, bitmap, newConversationId, currentTime)
            }
        } else {

            sendMessage(prompt, bitmap, _currentConversationId!!, currentTime)
        }
    }
}
