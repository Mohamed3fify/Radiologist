package com.example.radiologist.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.radiologist.model.Chat
import com.example.radiologist.logIn.google.UserData
import com.example.radiologist.model.AppUser
import com.example.radiologist.model.Constants
import com.example.radiologist.model.Conversation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Date
import java.util.UUID

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

    fun deleteConversation(
        conversationId: String,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        val conversationRef = Firebase.firestore
            .collection(Conversation.COLLECTION_NAME)
            .document(conversationId)

        conversationRef.collection(Chat.COLLECTION_NAME)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val batch = Firebase.firestore.batch()

                    for (document in task.result) {
                        val chatRef = conversationRef.collection(Chat.COLLECTION_NAME).document(document.id)
                        val bitmapUri = document.getString("bitmapUri")
                        if (bitmapUri != null) {
                            val storageRef = Firebase.storage.getReferenceFromUrl(bitmapUri)
                            storageRef.delete()
                                .addOnFailureListener { e ->
                                    Log.e("FirebaseUtils", "Error deleting image: ${e.message}")
                                }
                        }

                        batch.delete(chatRef)
                    }
                    batch.commit()
                        .addOnCompleteListener { batchTask ->
                            if (batchTask.isSuccessful) {
                                conversationRef.delete()
                                    .addOnSuccessListener(onSuccessListener)
                                    .addOnFailureListener(onFailureListener)
                            } else {
                                onFailureListener.onFailure(batchTask.exception!!)
                            }
                        }
                } else {
                    onFailureListener.onFailure(task.exception!!)
                }
            }
    }


fun saveChatMessage(
    chat: Chat,
    conversationId: String,
) {
        val documentReference = Firebase.firestore
            .collection(Conversation.COLLECTION_NAME)
            .document(conversationId)
            .collection(Chat.COLLECTION_NAME)
            .document()

        chat.conversationId = conversationId
        chat.dateTime = Date().time

    val chatData = hashMapOf(
        "prompt" to chat.prompt,
        "isFromUser" to chat.isFromUser,
        "conversationId" to chat.conversationId,
        "dateTime" to chat.dateTime,
    )

        if (chat.bitmap != null) {
            val storageRef = Firebase.storage.reference
            val imageRef = storageRef.child("images/${UUID.randomUUID()}.png")
            val stream = ByteArrayOutputStream()
            chat.bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
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

                    chatData["bitmapUri"] = downloadUri.toString()
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

            chatData["bitmapUri"] = null
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

 private fun getChatMessages(
    conversationId: String,
    onComplete: (List<Chat>) -> Unit
 ) {
        val chatList = mutableListOf<Chat>()
        val documentReference = Firebase.firestore
            .collection(Conversation.COLLECTION_NAME)
            .document(conversationId)
            .collection(Chat.COLLECTION_NAME)
            .orderBy("dateTime", Query.Direction.DESCENDING)

        documentReference.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val chat = document.toObject(Chat::class.java)

                    if (document.contains("isFromUser")) {
                        chat.isFromUser = document.getBoolean("isFromUser") ?: false
                    }

                    chatList.add(chat)
                }
                onComplete(chatList)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseUtils", "Error retrieving messages: ${e.message}")
            }
    }

 private fun downloadImage(
     uri: String,
     onComplete: (Bitmap?) -> Unit
 ) {
        val storageRef = Firebase.storage.getReferenceFromUrl(uri)

        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            onComplete(bitmap)
        }.addOnFailureListener { e ->
            Log.e("FirebaseUtils", "Error downloading image: ${e.message}")
            onComplete(null)
        }
    }
@OptIn(DelicateCoroutinesApi::class)
fun fetchChatMessagesWithImages(
   conversationId: String,
   onComplete: (List<Chat>) -> Unit
) {
        getChatMessages(conversationId) { chatList ->
            val downloadJobs = chatList.map { chat ->
                if (chat.bitmapUri != null) {
                    val uri = chat.bitmapUri!!
                    CompletableDeferred<Bitmap?>().apply {
                        downloadImage(uri) { bitmap ->
                            chat.bitmap = bitmap
                            complete(bitmap)
                        }
                    }
                } else {
                    CompletableDeferred<Bitmap?>().apply {
                        complete(null)
                    }
                }
            }
            GlobalScope.launch {
                downloadJobs.awaitAll()
                onComplete(chatList)
            }
        }
    }


}



