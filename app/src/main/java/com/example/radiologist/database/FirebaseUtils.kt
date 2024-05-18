package com.example.radiologist.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.radiologist.data.Chat
import com.example.radiologist.logIn.google.UserData
import com.example.radiologist.model.AppUser
import com.example.radiologist.model.Constants
import com.example.radiologist.model.Conversation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineStart
import java.io.ByteArrayOutputStream
import java.util.Date
import java.util.UUID

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object FirebaseUtils {
    private val auth = Firebase.auth
    fun addUser(
        user: AppUser,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        Firebase.firestore.collection(AppUser.COLLECTION_NAME).document(user.uid!!)
            .set(user)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun getUser(
        uid: String,
        onSuccessListener: OnSuccessListener<DocumentSnapshot>,
        onFailureListener: OnFailureListener
    ) {
        Firebase.firestore.collection(AppUser.COLLECTION_NAME)
            .document(uid)
            .get()
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun getGoogleSignInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            userName = displayName,
            email = email,
            profilePictureUrl = photoUrl?.toString()
        )
    }

    fun saveConversation(
        conversation: Conversation,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {

        val documentReference =
            Firebase.firestore
                .collection(Conversation.COLLECTION_NAME)
                .document()
        conversation.id = documentReference.id
        documentReference
            .set(conversation)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }


    fun getConversation(
        userId: String,
        onSuccessListener: OnSuccessListener<QuerySnapshot>,
        onFailureListener: OnFailureListener
    ) {
        Firebase.firestore
            .collection(Conversation.COLLECTION_NAME)
            .whereEqualTo(Constants.USER_ID, userId)
            .get()
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun saveChatMessage(
        chat: Chat,
        conversationId: String
    ) {
        val documentReference = Firebase.firestore
            .collection(Conversation.COLLECTION_NAME)
            .document(conversationId)
            .collection(Chat.COLLECTION_NAME)
            .document()

        chat.conversationId = conversationId
        chat.dateTime = Date().time

        if (chat.bitmap != null) {
            val storageRef = Firebase.storage.reference
            val imageRef = storageRef.child("images/${UUID.randomUUID()}.png")
            val stream = ByteArrayOutputStream()
            chat.bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val uploadTask = imageRef.putBytes(stream.toByteArray())

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    val chatData = hashMapOf(
                        "prompt" to chat.prompt,
                        "bitmapUri" to downloadUri.toString(),
                        "isFromUser" to chat.isFromUser,
                        "conversationId" to chat.conversationId,
                        "dateTime" to chat.dateTime
                    )

                    documentReference
                        .set(chatData)
                        .addOnSuccessListener {
                            Log.d("FirebaseUtils", "Message saved successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirebaseUtils", "Error saving message: ${e.message}")
                        }
                } else {
                    Log.e("FirebaseUtils", "Error getting download URL")
                }
            }
        } else {

            val chatData = hashMapOf(
                "prompt" to chat.prompt,
                "bitmapUri" to null,
                "isFromUser" to chat.isFromUser,
                "conversationId" to chat.conversationId,
                "dateTime" to chat.dateTime
            )
            documentReference
                .set(chatData)
                .addOnSuccessListener {
                    Log.d("FirebaseUtils", "message saved successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseUtils", "Error saving  message: ${e.message}")
                }
        }
    }

    fun fetchChatMessages(
        conversationId: String,
        callback: (List<Chat>) -> Unit
    ) {
        val firestore = Firebase.firestore
        val chatCollectionRef = firestore.collection(Conversation.COLLECTION_NAME)
            .document(conversationId)
            .collection(Chat.COLLECTION_NAME)

        chatCollectionRef.orderBy("dateTime").get()
            .addOnSuccessListener { documents ->
                val chatList = mutableListOf<Chat>()
                for (document in documents) {
                    val chat = document.toObject(Chat::class.java)
                    chatList.add(chat)
                }
                callback(chatList)
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseUtils", "Error getting documents: ", exception)
            }
    }

}

