package com.example.suararakyatv2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.suararakyatv2.databinding.HomeactivityBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeactivityBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setupObservers()
        setupClickListeners()
        
        viewModel.fetchUserData()
    }

    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is HomeUiState.Loading -> {
                    // Show a loading spinner if available in the UI
                }
                is HomeUiState.Success -> {
                    binding.tvGreeting.text = getString(R.string.greeting_format, state.firstName)
                }
                is HomeUiState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                is HomeUiState.Unauthenticated -> {
                    navigateToLogin()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        binding.cvReportIssue.setOnClickListener {
            startActivity(Intent(this, CreateReportActivity::class.java))
        }

        binding.cvTrackReport.setOnClickListener {
            Toast.makeText(this, getString(R.string.feature_in_development), Toast.LENGTH_SHORT).show()
        }

        binding.cvCommunity.setOnClickListener {
            Toast.makeText(this, getString(R.string.feature_in_development), Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
