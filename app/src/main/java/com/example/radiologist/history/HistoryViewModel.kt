package com.example.radiologist.history

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiologist.model.EventBas
import com.example.radiologist.model.SharedInterface
import com.example.radiologist.database.FirebaseUtils
import com.example.radiologist.model.Conversation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    val event = mutableStateOf<HistoryEvent>(HistoryEvent.Idle)
    val conversationsList = mutableStateListOf<Conversation>()
    val events = mutableStateOf<HistoryEvent>(HistoryEvent.Idle)

    init {
        getConversationsFromFirestore()
    }


    fun getConversationsFromFirestore() {
        val userId = Firebase.auth.currentUser?.uid ?: return
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

    fun deleteConversation(conversationId: String) {
        FirebaseUtils.deleteConversation(
            conversationId,
            onSuccessListener = {
                viewModelScope.launch {
                    EventBas.postEvent(SharedInterface.DeleteCurrentConversation(conversationId))
                    getConversationsFromFirestore()
                }
            }, onFailureListener = {
                Log.e("TAG", "deleteConversation: ${it.message}")
            }
        )
    }
    fun resetEventState() {
        event.value = HistoryEvent.Idle
    }

}
