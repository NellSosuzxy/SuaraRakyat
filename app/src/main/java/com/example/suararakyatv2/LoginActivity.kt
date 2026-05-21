package com.example.suararakyatv2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.suararakyatv2.databinding.LoginactivityBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginactivityBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> {
                    binding.btnSignIn.isEnabled = false
                    binding.btnSignIn.text = getString(R.string.signing_in)
                }
                is LoginState.Success -> {
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }
                is LoginState.Error -> {
                    binding.btnSignIn.isEnabled = true
                    binding.btnSignIn.text = getString(R.string.sign_in)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSignIn.setOnClickListener {
            viewModel.signIn(binding.etEmail.text.toString(), binding.etPassword.text.toString())
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, CreateActivity::class.java))
        }

        binding.tvGuest.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        
        // Placeholder for future OAuth integrations
        binding.btnGoogle.setOnClickListener { showFeatureInDevelopment() }
        binding.btnFacebook.setOnClickListener { showFeatureInDevelopment() }
        binding.tvForgotPassword.setOnClickListener { showFeatureInDevelopment() }
    }

    private fun showFeatureInDevelopment() {
        Toast.makeText(this, getString(R.string.feature_in_development), Toast.LENGTH_SHORT).show()
    }
}
