package com.example.radiologist.database

import com.example.radiologist.logIn.google.UserData
import com.example.radiologist.model.ChatState
import com.example.radiologist.model.AppUser
import com.example.radiologist.model.Conversation
import com.example.radiologist.model.Message
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
    fun getGoogleSignInUser() : UserData? = auth.currentUser?.run{
        UserData(
            userId = uid,
            userName = displayName,
            email = email,
            profilePictureUrl = photoUrl?.toString()
        )
    }


    fun addConversation(
        conversation: Conversation,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        val documentReference =
            Firebase.firestore
                .collection(Conversation.COLLECTION_NAME)
                .document()
        conversation.id = documentReference.id
        documentReference.set(conversation)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun getConversation(
        onSuccessListener: OnSuccessListener<QuerySnapshot>,
        onFailureListener: OnFailureListener
    ) {
        Firebase.firestore.collection(Conversation.COLLECTION_NAME)
            .get()
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun addMessage(
        conversationId: String,
        message: Message,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        Firebase.firestore.collection(Message.MESSAGE_COLLECTION)
            .document(message.conversationId!!)
            .collection(ChatState.COLLECTION_NAME)
            .document()
            .set(message)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)

    }

    fun getMessages(
        conversationId: String,
        snapshotListener: EventListener<QuerySnapshot>
    ) {
        Firebase.firestore.collection(Message.MESSAGE_COLLECTION)
            .document(conversationId)
            .collection(Message.MESSAGE_COLLECTION)
            .addSnapshotListener(snapshotListener)
    }

}