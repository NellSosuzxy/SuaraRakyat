package com.example.suararakyatv2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}

class CreateUserViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _signUpState = MutableLiveData<SignUpState>(SignUpState.Idle)
    val signUpState: LiveData<SignUpState> = _signUpState

    fun performSignUp(fullName: String, email: String, phone: String, password: String, role: String) {
        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || role.isBlank() || role == "Select your role") {
            _signUpState.value = SignUpState.Error("All fields are required")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _signUpState.value = SignUpState.Error("Please enter a valid email")
            return
        }

        if (password.length < 6) {
            _signUpState.value = SignUpState.Error("Password must be at least 6 characters")
            return
        }

        _signUpState.value = SignUpState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        saveUserToFirestore(uid, fullName, email, phone, role)
                    } else {
                        _signUpState.value = SignUpState.Error("Failed to retrieve user ID")
                    }
                } else {
                    _signUpState.value = SignUpState.Error(task.exception?.message ?: "Registration failed")
                }
            }
    }

    private fun saveUserToFirestore(uid: String, fullName: String, email: String, phone: String, role: String) {
        val userData = hashMapOf(
            "fullName" to fullName,
            "email" to email,
            "phone" to phone,
            "role" to role
        )

        firestore.collection("users").document(uid)
            .set(userData)
            .addOnSuccessListener {
                _signUpState.value = SignUpState.Success
            }
            .addOnFailureListener { e ->
                _signUpState.value = SignUpState.Error("Failed to save user data: ${e.message}")
            }
    }
}
