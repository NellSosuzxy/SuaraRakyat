package com.example.suararakyatv2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableLiveData<HomeUiState>()
    val uiState: LiveData<HomeUiState> = _uiState

    fun fetchUserData() {
        val user = auth.currentUser
        if (user == null) {
            _uiState.value = HomeUiState.Unauthenticated
            return
        }

        _uiState.value = HomeUiState.Loading
        firestore.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val fullName = document.getString("fullName") ?: "User"
                    val firstName = fullName.split(" ").firstOrNull() ?: "User"
                    _uiState.value = HomeUiState.Success(firstName)
                } else {
                    _uiState.value = HomeUiState.Success("User")
                }
            }
            .addOnFailureListener {
                _uiState.value = HomeUiState.Error("Failed to load user data.")
            }
    }

    fun logout() {
        auth.signOut()
        _uiState.value = HomeUiState.Unauthenticated
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val firstName: String) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
    object Unauthenticated : HomeUiState()
}
