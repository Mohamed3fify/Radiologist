package com.example.radiologist.chatBot

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiologist.api.ApiManager
import com.example.radiologist.api.model.ModelResponse
import com.example.radiologist.model.EventBas
import com.example.radiologist.model.SharedInterface
import com.example.radiologist.model.Chat
import com.example.radiologist.gemini.GeminiData
import com.example.radiologist.database.FirebaseUtils
import com.example.radiologist.database.FirebaseUtils.saveChatMessage
import com.example.radiologist.model.Conversation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.util.UUID

class ChatViewModel : ViewModel() {

    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    var conversation: Conversation? = null

    var chat: Chat? = null

    private var _currentConversationId: String? = null

    init {
        viewModelScope.launch {
            EventBas.events.collect { event ->
                when (event) {
                    is SharedInterface.DeleteCurrentConversation -> {
                        if (event.conversationId == _currentConversationId) {
                            resetChatScreen()
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.SendPrompt -> {
                if (event.prompt.isNotEmpty()) {
                    _chatState.update {
                        it.copy(isTyping = true)
                    }
                    val conversationId = event.conversationId
                    val dateTime = event.dateTime

                    onSendPrompt(event.prompt, event.bitmap, conversationId!!, dateTime)
                    viewModelScope.launch {
                        try {
                            if (event.bitmap != null) {
                                //getResponseWithImgFromGemini(event)
                                getResponseWithImgFromModel(event)
                            } else {
                                getResponseFromGemini(event)
                            }
                        } catch (e: Exception) {
                            Log.d("ChatViewModel", e.message!!)
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

            is ChatUiEvent.ResetChatScreen -> {
                resetChatScreen()
            }

            is ChatUiEvent.LoadConversation -> {
                _currentConversationId = event.conversationId
                _chatState.update { it.copy(isLoading = true) }
                fetchMessages(event.conversationId)
            }

        }
    }

    private fun addPrompt(
        prompt: String,
        bitmap: Bitmap?,
        conversationId: String,
        dateTime: Long,
    ) {

        val chat = Chat(
            prompt,
            bitmap,
            true,
            conversation?.id,
            dateTime = dateTime,
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

    private suspend fun getResponseWithImgFromGemini(event: ChatUiEvent.SendPrompt) {
        val response = GeminiData
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

    private suspend fun getResponseFromGemini(event: ChatUiEvent.SendPrompt) {
        val response = GeminiData.getResponse(
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

    private fun addConversation(
        name: String,
        dateTime: Long? = null,
        callback: (String) -> Unit
    ) {
        val userId = Firebase.auth.currentUser?.uid ?: return

        val conversation = Conversation(
            name = name,
            id = UUID.randomUUID().toString(),
            userId = userId,
            dateTime = dateTime
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
            addConversation(prompt, currentTime) { newConversationId ->
                _currentConversationId = newConversationId

                addPrompt(prompt, bitmap, newConversationId, currentTime)
            }
        } else {
            addPrompt(prompt, bitmap, _currentConversationId!!, currentTime)
        }
    }

    private fun fetchMessages(conversationId: String) {

        FirebaseUtils.fetchChatMessagesWithImages(conversationId) { chatMessages ->
            _chatState.update { currentState ->
                currentState.copy(
                    chatList = chatMessages.toMutableList(),
                    isLoading = false
                )
            }
        }
    }

    private fun resetChatScreen() {
        _chatState.update {
            it.copy(
                chatList = mutableListOf(),
                prompt = "",
                bitmap = null,
                isTyping = false
            )
        }
        conversation = null
        chat = null
        _currentConversationId = null
    }

    private suspend fun getResponseWithImgFromModel(event: ChatUiEvent.SendPrompt) {
        val imageBody = event.bitmap?.toMultipartBodyPart("image")

        try {
            val response = withContext(Dispatchers.IO) {
                ApiManager.modelServices.getResponseWithImg(event.prompt, imageBody!!).execute()
            }
            if (response.isSuccessful) {
                response.body()?.let { chatResponse ->
                    _chatState.update {
                        it.copy(
                            chatList = it.chatList.toMutableList().apply {
                                add(0, Chat(
                                    prompt = chatResponse.answer ?: "",
                                    bitmap = null,
                                    isFromUser = false,
                                    conversationId = event.conversationId,
                                    dateTime = System.currentTimeMillis()
                                ))
                            }
                        )
                    }
                    saveChatMessage(chatResponse.toChat(), _currentConversationId!!)
                }
            } else {
                Log.d("ChatViewModel", "Response not successful: ${response.message()}")
                handleErrorResponse(response.code().toString(), response.message())

            }
        } catch (e: Exception) {
            Log.d("ChatViewModel", "Error: ${e.message}")
            handleErrorResponse(null ,e.message)
        }
    }
    private fun ModelResponse.toChat(): Chat {
        return Chat(
            prompt = this.answer ?: "",
            bitmap = null,
            isFromUser = false,
            conversationId = _currentConversationId!!,
            dateTime = System.currentTimeMillis()
        )
    }

    private fun Bitmap.toMultipartBodyPart(partName: String): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody("image/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, "image.png", requestBody)
    }

    private fun handleErrorResponse(errorCode: String?, errorMessage: String?) {
        val errorChat = Chat(
            prompt = "Error: Code $errorCode, Message: ${errorMessage ?: "No message"}",
            bitmap = null,
            isFromUser = false,
            conversationId = _currentConversationId,
            dateTime = System.currentTimeMillis()
        )
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, errorChat)
                }
            )
        }
        saveChatMessage(errorChat, _currentConversationId!!)
    }
}
