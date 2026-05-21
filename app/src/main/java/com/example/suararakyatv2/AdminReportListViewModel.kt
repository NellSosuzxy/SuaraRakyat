package com.example.suararakyatv2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed class ReportListState {
    object Loading : ReportListState()
    data class Success(val reports: List<Report>) : ReportListState()
    data class Error(val message: String) : ReportListState()
    object Empty : ReportListState()
}

class AdminReportListViewModel : ViewModel() {
    private val repository = ReportRepository()

    private val _listState = MutableLiveData<ReportListState>()
    val listState: LiveData<ReportListState> = _listState

    fun loadReports(category: String) {
        _listState.value = ReportListState.Loading
        viewModelScope.launch {
            try {
                val reports = repository.getReportsByCategory(category)
                if (reports.isEmpty()) {
                    _listState.value = ReportListState.Empty
                } else {
                    _listState.value = ReportListState.Success(reports)
                }
            } catch (e: Exception) {
                _listState.value = ReportListState.Error("Failed to load reports: ${e.message}")
            }
        }
    }
}
