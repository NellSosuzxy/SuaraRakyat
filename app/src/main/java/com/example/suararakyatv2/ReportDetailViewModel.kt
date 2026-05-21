package com.example.suararakyatv2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suararakyatv2.utils.UiState
import kotlinx.coroutines.launch

class ReportDetailViewModel : ViewModel() {
    private val repository = ReportRepository()

    private val _uiState = MutableLiveData<UiState<Map<String, Any>>>()
    val uiState: LiveData<UiState<Map<String, Any>>> = _uiState

    fun loadReport(id: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val report = repository.getReportDetailById(id)
                if (report != null) _uiState.value = UiState.Success(report)
                else _uiState.value = UiState.Error("Report not found.")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load report.")
            }
        }
    }
}