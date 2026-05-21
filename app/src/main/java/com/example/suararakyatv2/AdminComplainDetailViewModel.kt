package com.example.suararakyatv2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suararakyatv2.utils.UiState
import kotlinx.coroutines.launch

class AdminComplainDetailViewModel : ViewModel() {
    private val reportRepo = ReportRepository()
    private val userRepo = UserRepository()

    private val _uiState = MutableLiveData<UiState<Pair<Map<String, Any>, String>>>()
    val uiState: LiveData<UiState<Pair<Map<String, Any>, String>>> = _uiState

    private val _updateState = MutableLiveData<UiState<Unit>>()
    val updateState: LiveData<UiState<Unit>> = _updateState

    fun loadDetails(id: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val reportData = reportRepo.getReportDetailById(id)
                if (reportData != null) {
                    val reporterId = reportData["reporterId"] as? String
                    val userName = if (!reporterId.isNullOrEmpty()) {
                        userRepo.getUserNameById(reporterId) ?: "Name not found"
                    } else "Anonymous"
                    _uiState.value = UiState.Success(Pair(reportData, userName))
                } else {
                    _uiState.value = UiState.Error("Report not found.")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load report.")
            }
        }
    }

    fun updateReportStatus(id: String, newStatus: String, feedback: String?) {
        _updateState.value = UiState.Loading
        viewModelScope.launch {
            val updates = mutableMapOf<String, Any>("status" to newStatus)
            if (feedback != null) updates["feedback"] = feedback
            
            val success = reportRepo.updateReport(id, updates)
            if (success) _updateState.value = UiState.Success(Unit)
            else _updateState.value = UiState.Error("Failed to update report.")
        }
    }
}