package com.example.radiologist.history

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.radiologist.database.FirebaseUtils
import com.example.radiologist.model.Conversation
import com.example.radiologist.register.RegisterEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.UUID

class HistoryViewModel : ViewModel() {
    private val isLoading = mutableStateOf(false)
    val event = mutableStateOf<HistoryEvent>(HistoryEvent.Idle)
    val conversationsList = mutableStateListOf<Conversation>()
    val conversationId = UUID.randomUUID().toString()
    val events = mutableStateOf<HistoryEvent>(HistoryEvent.Idle)


    init {
        getConversationsFromFirestore()
    }


    fun getConversationsFromFirestore() {
        val userId = Firebase.auth.currentUser?.uid ?: return
        isLoading.value = true
        FirebaseUtils.getConversation(
            userId = userId,
            onSuccessListener = {
            conversationsList.clear()
            val list = it.toObjects(Conversation::class.java)
            conversationsList.addAll(list)
        }, onFailureListener = {
            Log.e("TAG", "getConversationsFromFirestore: ${it.message}")
        }
        )
    }
    fun navigateToChatScreen(conversation: Conversation) {
        event.value = HistoryEvent.NavigateToChatScreen(conversation)
    }

    fun resetEventState() {
        event.value = HistoryEvent.Idle
    }
}