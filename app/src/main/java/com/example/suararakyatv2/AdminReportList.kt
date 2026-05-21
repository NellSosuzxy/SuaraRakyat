package com.example.suararakyatv2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suararakyatv2.databinding.AdminreportlistBinding

class AdminReportList : AppCompatActivity() {

    companion object {
        const val EXTRA_REPORT_CATEGORY = "report_category"
    }

    private lateinit var binding: AdminreportlistBinding
    private val viewModel: AdminReportListViewModel by viewModels()
    private lateinit var adapter: ReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminreportlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        val category = intent.getStringExtra(EXTRA_REPORT_CATEGORY)

        if (category != null) {
            binding.toolbar.title = getString(R.string.category_reports_format, category)
            setupObservers()
            viewModel.loadReports(category)
        } else {
            Toast.makeText(this, getString(R.string.error_invalid_category), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = ReportAdapter { reportId ->
            openDetailActivityWithId(reportId)
        }
        binding.rvAdminReports.layoutManager = LinearLayoutManager(this)
        binding.rvAdminReports.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.listState.observe(this) { state ->
            when (state) {
                is ReportListState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvAdminReports.visibility = View.GONE
                }
                is ReportListState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvAdminReports.visibility = View.VISIBLE
                    adapter.submitList(state.reports)
                }
                is ReportListState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvAdminReports.visibility = View.GONE
                    Toast.makeText(this, getString(R.string.no_reports_found), Toast.LENGTH_SHORT).show()
                }
                is ReportListState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvAdminReports.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openDetailActivityWithId(reportId: String) {
        val intent = Intent(this, AdminComplainDetail::class.java)
        intent.putExtra("EXTRA_REPORT_ID", reportId)
        startActivity(intent)
    }
}