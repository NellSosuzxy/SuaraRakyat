package com.example.suararakyatv2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.suararakyatv2.databinding.AdmincomplaindetailBinding
import com.example.suararakyatv2.utils.FileHelper
import com.example.suararakyatv2.utils.UiState
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class AdminComplainDetail : AppCompatActivity() {

    private lateinit var binding: AdmincomplaindetailBinding
    private val viewModel: AdminComplainDetailViewModel by viewModels()
    private var reportId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdmincomplaindetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinner()
        setupClickListeners()
        setupObservers()

        reportId = intent.getStringExtra("EXTRA_REPORT_ID")
        Log.d("ReportDetail", "Received Report ID: $reportId")

        if (reportId == null) {
            Toast.makeText(this, getString(R.string.error_report_id_not_found), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        viewModel.loadDetails(reportId!!)
    }

    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val reportData = state.data.first
                    val userName = state.data.second

                    binding.txtUserName.text = userName
                    binding.txtDetailComplaint.text = reportData["description"] as? String ?: ""
                    
                    val currentFeedback = reportData["feedback"] as? String ?: ""
                    binding.inputFeedback.setText(currentFeedback)

                    (reportData["timestamp"] as? Timestamp)?.toDate()?.let { date ->
                        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
                        binding.txtDate.text = sdf.format(date)
                    }

                    FileHelper.handleEvidence(this, reportData["imageUrl"] as? String, binding.imgComplaint)
                    
                    val currentStatus = reportData["status"] as? String ?: ""
                    val spinnerAdapter = binding.spinnerStatus.adapter as ArrayAdapter<String>
                    val position = spinnerAdapter.getPosition(currentStatus)
                    if (position >= 0) {
                        binding.spinnerStatus.setSelection(position)
                    }
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        viewModel.updateState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, getString(R.string.report_updated_success), Toast.LENGTH_SHORT).show()
                    finish()
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupSpinner() {
package com.example.suararakyatv2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.suararakyatv2.databinding.AdmincomplaindetailBinding
import com.example.suararakyatv2.utils.FileHelper
import com.example.suararakyatv2.utils.UiState
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class AdminComplainDetail : AppCompatActivity() {

    private lateinit var binding: AdmincomplaindetailBinding
    private val viewModel: AdminComplainDetailViewModel by viewModels()
    private var reportId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdmincomplaindetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinner()
        setupClickListeners()
        setupObservers()

        reportId = intent.getStringExtra("EXTRA_REPORT_ID")
        Log.d("ReportDetail", "Received Report ID: $reportId")

        if (reportId == null) {
            Toast.makeText(this, getString(R.string.error_report_id_not_found), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        viewModel.loadDetails(reportId!!)
    }

    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val reportData = state.data.first
                    val userName = state.data.second

                    binding.txtUserName.text = userName
                    binding.txtDetailComplaint.text = reportData["description"] as? String ?: ""
                    
                    val currentFeedback = reportData["feedback"] as? String ?: ""
                    binding.inputFeedback.setText(currentFeedback)

                    (reportData["timestamp"] as? Timestamp)?.toDate()?.let { date ->
                        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
                        binding.txtDate.text = sdf.format(date)
                    }

                    FileHelper.handleEvidence(this, reportData["imageUrl"] as? String, binding.imgComplaint)
                    
                    val currentStatus = reportData["status"] as? String ?: ""
                    val spinnerAdapter = binding.spinnerStatus.adapter as ArrayAdapter<String>
                    val position = spinnerAdapter.getPosition(currentStatus)
                    if (position >= 0) {
                        binding.spinnerStatus.setSelection(position)
                    }
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        viewModel.updateState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, getString(R.string.report_updated_success), Toast.LENGTH_SHORT).show()
                    finish()
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupSpinner() {
