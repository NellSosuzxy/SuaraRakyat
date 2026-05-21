package com.example.suararakyatv2

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import com.google.firebase.firestore.FieldValue

sealed class ReportSubmissionState {
    object Idle : ReportSubmissionState()
    object Loading : ReportSubmissionState()
    object Success : ReportSubmissionState()
    data class Error(val message: String) : ReportSubmissionState()
}

class CreateReportViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _submissionState = MutableLiveData<ReportSubmissionState>(ReportSubmissionState.Idle)
    val submissionState: LiveData<ReportSubmissionState> = _submissionState

    fun submitReport(
        title: String,
        category: String,
        description: String,
        location: String,
        isAnonymous: Boolean,
        selectedFileUri: Uri?
    ) {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            _submissionState.value = ReportSubmissionState.Error("Authentication error: User not logged in.")
            return
        }

        if (title.isBlank() || description.isBlank() || location.isBlank()) {
            _submissionState.value = ReportSubmissionState.Error("Title, Description, and Location are required.")
            return
        }

        _submissionState.value = ReportSubmissionState.Loading

        if (selectedFileUri != null) {
            uploadFileAndSave(selectedFileUri, title, category, description, location, isAnonymous, currentUserId)
        } else {
            saveToFirestore(title, category, description, location, isAnonymous, currentUserId, null)
        }
    }

    private fun uploadFileAndSave(
        uri: Uri,
        title: String,
        category: String,
        description: String,
        location: String,
        isAnonymous: Boolean,
        currentUserId: String
    ) {
        val fileName = UUID.randomUUID().toString()
        val storageRef = storage.getReference("/evidence/$fileName")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    saveToFirestore(title, category, description, location, isAnonymous, currentUserId, downloadUrl.toString())
                }
            }
            .addOnFailureListener { e ->
                _submissionState.value = ReportSubmissionState.Error("File upload failed: $e.message")
            }
    }

    private fun saveToFirestore(
        title: String,
        category: String,
        description: String,
        location: String,
        isAnonymous: Boolean,
        currentUserId: String,
        evidenceUrl: String?
    ) {
        val reporterIdToSave = if (isAnonymous) "anonymous" else currentUserId

        val report = hashMapOf(
            "title" to title,
            "description" to description,
            "category" to category,
            "location" to location,
            "timestamp" to FieldValue.serverTimestamp(),
            "status" to "Pending",
            "imageUrl" to (evidenceUrl ?: ""),
            "feedback" to "",
            "reporterId" to reporterIdToSave
        )

        firestore.collection("reports")
            .add(report)
            .addOnSuccessListener {
                _submissionState.value = ReportSubmissionState.Success
            }
            .addOnFailureListener { e ->
                _submissionState.value = ReportSubmissionState.Error("Database error: $e.message")
            }
    }
}
