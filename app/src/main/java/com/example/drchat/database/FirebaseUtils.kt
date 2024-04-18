package com.example.drchat.database

import com.example.drchat.logIn.google.UserData
import com.example.drchat.model.ChatState
import com.example.drchat.model.AppUser
import com.example.drchat.model.Conversation
import com.example.drchat.model.DataUtils.firebaseUser
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
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
    fun getSignInUser() : UserData? = auth.currentUser?.run{
        UserData(
            userId = uid,
            userName = displayName,
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
        message: ChatState,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        Firebase.firestore.collection(Conversation.COLLECTION_NAME)
            .document(message.conversationId!!)
            .collection(ChatState.COLLECTION_NAME)
            .document()
            .set(message)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)

    }

    fun getMessages(
        roomId: String,
        snapshotListener: EventListener<QuerySnapshot>
    ) {
        Firebase.firestore.collection(Conversation.COLLECTION_NAME)
            .document(roomId)
            .collection(ChatState.COLLECTION_NAME)
            .addSnapshotListener(snapshotListener)
    }

}