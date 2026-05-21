package com.example.suararakyatv2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.suararakyatv2.databinding.CreateactivityBinding

class CreateActivity : AppCompatActivity() {

    private lateinit var binding: CreateactivityBinding
    private val viewModel: CreateUserViewModel by viewModels()

    private var selectedRole: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setupSpinner()
        setupClickListeners()
        setupObservers()
    }

    private fun setupSpinner() {
        val roles = resources.getStringArray(R.array.user_roles)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRoles.adapter = adapter

        binding.spinnerRoles.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRole = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedRole = ""
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnCreateAccount.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            viewModel.performSignUp(fullName, email, phone, password, selectedRole)
        }

        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }

        binding.btnGoogle.setOnClickListener {
            Toast.makeText(this, getString(R.string.feature_in_development), Toast.LENGTH_SHORT).show()
        }

        binding.btnFacebook.setOnClickListener {
            Toast.makeText(this, getString(R.string.feature_in_development), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.signUpState.observe(this) { state ->
            when (state) {
                is SignUpState.Loading -> {
                    binding.btnCreateAccount.isEnabled = false
                    binding.btnCreateAccount.text = getString(R.string.creating_account)
                }
                is SignUpState.Success -> {
                    Toast.makeText(this, getString(R.string.account_created_success), Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }
                is SignUpState.Error -> {
                    binding.btnCreateAccount.isEnabled = true
                    binding.btnCreateAccount.text = getString(R.string.create_account)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                is SignUpState.Idle -> {
                    binding.btnCreateAccount.isEnabled = true
                    binding.btnCreateAccount.text = getString(R.string.create_account)
                }
            }
        }
    }
}
