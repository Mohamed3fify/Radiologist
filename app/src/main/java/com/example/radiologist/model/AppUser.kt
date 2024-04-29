package com.example.radiologist.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppUser(
    val uid: String? = null,
    val firstName: String? = null,
    val email: String? = null,
    val displayName :String? =null
) : Parcelable {
    companion object {
        const val COLLECTION_NAME = "users"
    }
}
