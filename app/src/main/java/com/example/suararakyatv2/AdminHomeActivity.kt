package com.example.suararakyatv2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.example.suararakyatv2.databinding.AdminhomeactivityBinding

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var binding: AdminhomeactivityBinding

    companion object {
        const val EXTRA_REPORT_CATEGORY = "report_category"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminhomeactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.cardABullying.setOnClickListener {
            openReportsList(getString(R.string.bullying))
        }

        binding.cardAEnv.setOnClickListener {
            openReportsList(getString(R.string.environmental_issues))
        }

        binding.cardASecurity.setOnClickListener {
            openReportsList(getString(R.string.security_issues))
        }
    }

    private fun openReportsList(category: String) {
        val intent = Intent(this, AdminReportList::class.java).apply {
            putExtra(EXTRA_REPORT_CATEGORY, category)
        }
        startActivity(intent)
    }
}
