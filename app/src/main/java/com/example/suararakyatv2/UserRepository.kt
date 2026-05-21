package com.example.suararakyatv2

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getUserNameById(userId: String): String? = suspendCancellableCoroutine { continuation ->
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) continuation.resume(doc.getString("fullName"))
                else continuation.resume(null)
            }
            .addOnFailureListener { continuation.resumeWithException(it) }
    }
}