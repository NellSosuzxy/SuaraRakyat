package com.example.suararakyatv2

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ReportRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getReportsByCategory(category: String): List<Report> = suspendCancellableCoroutine { continuation ->
        firestore.collection("reports")
            .whereEqualTo("category", category)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val reportsList = documents.mapNotNull { doc ->
                    try {
                        Report(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            category = doc.getString("category") ?: "",
                            location = doc.getString("location") ?: "",
                            status = doc.getString("status") ?: "",
                            timestamp = doc.getTimestamp("timestamp")?.toDate(),
                            imageUrl = doc.getString("imageUrl") ?: "",
                            reporterId = doc.getString("reporterId") ?: ""
                        )
                    } catch (e: Exception) { null }
                }
                continuation.resume(reportsList)
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }

    suspend fun getReportDetailById(id: String): Map<String, Any>? = suspendCancellableCoroutine { continuation ->
        firestore.collection("reports").document(id).get()
            .addOnSuccessListener { doc ->
                if (doc.exists() && doc.data != null) {
                    continuation.resume(doc.data!!.plus("id" to doc.id))
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { continuation.resumeWithException(it) }
    }

    suspend fun updateReport(id: String, updates: Map<String, Any>): Boolean = suspendCancellableCoroutine { continuation ->
        firestore.collection("reports").document(id).update(updates)
            .addOnSuccessListener { continuation.resume(true) }
            .addOnFailureListener { continuation.resume(false) }
    }
}