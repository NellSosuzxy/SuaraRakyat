package com.example.suararakyatv2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.IOException
import android.content.ContentValues
import android.provider.MediaStore
import com.example.suararakyatv2.databinding.ReportdetailBinding
import com.example.suararakyatv2.utils.FileHelper
import com.example.suararakyatv2.utils.UiState
import com.google.firebase.Timestamp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportDetail : AppCompatActivity() {

    private lateinit var binding: ReportdetailBinding
    private val viewModel: ReportDetailViewModel by viewModels()
    private var currentReportId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReportdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupObservers()

        val reportId = intent.getStringExtra("EXTRA_REPORT_ID")
        if (reportId == null) {
            Toast.makeText(this, getString(R.string.error_report_id_not_found), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        viewModel.loadReport(reportId)
        currentReportId = reportId
    }

    private fun setupClickListeners() {
        binding.btnPrintPdf.setOnClickListener {
            generatePdfReportAsync()
        }

        binding.btnEmailReport.setOnClickListener {
            emailReport()
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val report = state.data
                    binding.detailReportTitle.text = report["title"] as? String ?: ""
                    binding.detailReportDescription.text = report["description"] as? String ?: ""
                    
                    (report["timestamp"] as? Timestamp)?.toDate()?.let { date ->
                        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
                        binding.detailReportDate.text = getString(R.string.submitted_on_format, sdf.format(date))
                    }
                    
                    val status = report["status"] as? String ?: "N/A"
                    binding.detailReportStatus.text = status
                    setStatusColor(status)
                    
                    val feedback = report["feedback"] as? String
                    if (feedback.isNullOrEmpty()) {
                        binding.detailAdminFeedback.text = getString(R.string.no_admin_feedback_yet)
                    } else {
                        binding.detailAdminFeedback.text = feedback
                    }
                    
                    FileHelper.handleEvidence(this, report["imageUrl"] as? String, binding.ivEvidencePreview)

                    if (status == "Pending") {
                        binding.btnUpdateComplaint.isEnabled = true
                        binding.btnUpdateComplaint.alpha = 1.0f
                        binding.btnUpdateComplaint.setOnClickListener {
                            Toast.makeText(this, getString(R.string.pending_complaint_update_msg), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        binding.btnUpdateComplaint.isEnabled = false
                        binding.btnUpdateComplaint.alpha = 0.5f
                        binding.btnUpdateComplaint.setOnClickListener {
                            Toast.makeText(this, getString(R.string.non_pending_complaint_update_msg), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun setStatusColor(status: String) {
        val colorRes = when (status) {
            "Pending" -> R.color.status_pending
            "In Progress" -> R.color.status_in_progress
            "Resolved" -> R.color.status_resolved
            else -> R.color.status_default
        }
        binding.detailReportStatus.setBackgroundResource(colorRes)
    }

    private fun generatePdfReportAsync(fileName: String = "report_${System.currentTimeMillis()}.pdf", showToast: Boolean = true) {
        // Safely launch on a background thread tied to the Activity lifecycle
        lifecycleScope.launch(Dispatchers.IO) {
            val uri = generatePdfReportBlocking(fileName)
            withContext(Dispatchers.Main) {
                if (uri != null && showToast) Toast.makeText(this@ReportDetail, getString(R.string.pdf_saved_to_downloads), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun generatePdfReportBlocking(fileName: String): Uri? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        var y = 80f

        paint.textSize = 20f
        canvas.drawText(binding.detailReportTitle.text.toString(), 50f, y, paint)
        y += 25f

        paint.textSize = 12f
        canvas.drawText(binding.detailReportDate.text.toString(), 50f, y, paint)
        y += 50f

        paint.textSize = 14f
        canvas.drawText("Description:", 50f, y, paint)
        y += 20f
        val descriptionLines = binding.detailReportDescription.text.toString().split("\n")
        for (line in descriptionLines) {
            canvas.drawText(line, 50f, y, paint)
            y += paint.fontSpacing
        }
        y += 30f

        paint.textSize = 14f
        canvas.drawText("Status: ${binding.detailReportStatus.text}", 50f, y, paint)
        y += 20f

        paint.textSize = 14f
        canvas.drawText("Admin Feedback:", 50f, y, paint)
        y += 20f
        val feedbackLines = binding.detailAdminFeedback.text.toString().split("\n")
        for (line in feedbackLines) {
            canvas.drawText(line, 50f, y, paint)
            y += paint.fontSpacing
        }
        y += 30f

        if (binding.ivEvidencePreview.visibility == View.VISIBLE) {
            val bitmap = getResizedBitmap(200, 200)
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 50f, y, paint)
            }
        }

        pdfDocument.finishPage(page)

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            try {
                resolver.openOutputStream(uri).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                pdfDocument.close()
            }
            return uri
        } else {
            pdfDocument.close()
            return null
        }
    }

    private fun getResizedBitmap(width: Int, height: Int): Bitmap? {
        val drawable = binding.ivEvidencePreview.drawable

        // Check if the drawable is a BitmapDrawable (from Glide).
        // This is crucial to prevent crashes on VectorDrawables (your placeholders).
        if (drawable is android.graphics.drawable.BitmapDrawable) {
            // If it's a bitmap, scale it and return it.
            val bitmap = drawable.bitmap
            return Bitmap.createScaledBitmap(bitmap, width, height, false)
        }

        // If it's not a bitmap (e.g., a placeholder), return null
        // so the PDF generation code can safely ignore it.
        return null
    }
    private fun emailReport() {
        val fileName = "report_${currentReportId}.pdf"
        lifecycleScope.launch(Dispatchers.IO) {
            val fileUri = generatePdfReportBlocking(fileName)
            withContext(Dispatchers.Main) {
                if (fileUri == null) {
                    Toast.makeText(this@ReportDetail, getString(R.string.error_generating_pdf_email), Toast.LENGTH_SHORT).show()
                    return@withContext
                }

                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_SUBJECT, "Complaint Report: ${binding.detailReportTitle.text}")
                    putExtra(Intent.EXTRA_TEXT, "Please find the attached complaint report.")
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send email..."))
                } catch (ex: android.content.ActivityNotFoundException) {
                    Toast.makeText(this@ReportDetail, getString(R.string.no_email_client), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}