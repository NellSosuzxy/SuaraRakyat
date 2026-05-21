package com.example.suararakyatv2

import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.activity.viewModels
import com.example.suararakyatv2.databinding.CreatereportactivityBinding
import com.google.android.gms.location.LocationServices
import java.util.Locale

class CreateReportActivity : AppCompatActivity() {

    private lateinit var binding: CreatereportactivityBinding
    private val viewModel: CreateReportViewModel by viewModels()

    private var selectedFileUri: Uri? = null

    companion object {
        const val EXTRA_REPORT_CATEGORY = "report_category"
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Location permission granted.", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_LONG).show()
                binding.btnUseCurrentLocation.isEnabled = true
                binding.btnUseCurrentLocation.text = "📍 Use Current Location"
            }
        }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedFileUri = uri
                binding.txtUpload.text = "File selected. Ready to upload."
                binding.txtUpload.setTextColor(ContextCompat.getColor(this, R.color.status_resolved))
            } else {
                Toast.makeText(this, "File selection cancelled.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreatereportactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupObservers()

        val preselectedCategory = intent.getStringExtra(EXTRA_REPORT_CATEGORY)
        preSelectCategory(preselectedCategory)
    }

    private fun setupClickListeners() {
        binding.btnSubmitReport.setOnClickListener { submitReport() }
        binding.btnUseCurrentLocation.setOnClickListener { useCurrentLocation() }
        binding.cardUpload.setOnClickListener { uploadEvidence() }
    }

    private fun setupObservers() {
        viewModel.submissionState.observe(this) { state ->
            when (state) {
                is ReportSubmissionState.Loading -> {
                    binding.btnSubmitReport.isEnabled = false
                    binding.btnSubmitReport.text = "Submitting..."
                }
                is ReportSubmissionState.Success -> {
                    Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_LONG).show()
                    finish()
                }
                is ReportSubmissionState.Error -> {
                    Toast.makeText(this, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                    binding.btnSubmitReport.isEnabled = true
                    binding.btnSubmitReport.text = "Submit Report"
                }
                else -> {
                    binding.btnSubmitReport.isEnabled = true
                    binding.btnSubmitReport.text = "Submit Report"
                }
            }
        }
    }

    private fun preSelectCategory(category: String?) {
        if (category == null) return
        val spinnerAdapter = binding.spinnerCategory.adapter as? ArrayAdapter<String>
        spinnerAdapter?.let {
            val position = it.getPosition(category)
            if (position >= 0) {
                binding.spinnerCategory.setSelection(position)
            }
        }
    }

    private fun uploadEvidence() {
        // [REFACTORED] Standard file picker for proper compatibility natively
        filePickerLauncher.launch("*/*")
    }

    private fun submitReport() {
        val title = binding.etReportTitle.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem?.toString() ?: ""
        val description = binding.etDescription.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val isAnonymous = binding.cbSubmitAnonymously.isChecked

        if (title.isEmpty()) {
            binding.etReportTitle.error = "Report title is required"
            return
        }
        if (description.isEmpty()) {
            binding.etDescription.error = "Description is required"
            return
        }
        if (location.isEmpty()) {
            binding.etLocation.error = "Location is required"
            return
        }

        viewModel.submitReport(title, category, description, location, isAnonymous, selectedFileUri)
    }

    private fun useCurrentLocation() {
        binding.btnUseCurrentLocation.isEnabled = false
        binding.btnUseCurrentLocation.text = "Fetching location..."

        when {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        try {
                            val geocoder = Geocoder(this, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            val addressText = addresses?.firstOrNull()?.getAddressLine(0) ?: "${location.latitude}, ${location.longitude}"
                            binding.etLocation.setText(addressText)
                        } catch (e: Exception) {
                            binding.etLocation.setText("${location.latitude}, ${location.longitude}")
                        }
                    } else {
                        Toast.makeText(this, "Location not found. Ensure GPS is on.", Toast.LENGTH_SHORT).show()
                    }
                    binding.btnUseCurrentLocation.isEnabled = true
                    binding.btnUseCurrentLocation.text = "📍 Use Current Location"
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to get location.", Toast.LENGTH_SHORT).show()
                    binding.btnUseCurrentLocation.isEnabled = true
                    binding.btnUseCurrentLocation.text = "📍 Use Current Location"
                }
        }
    }
}