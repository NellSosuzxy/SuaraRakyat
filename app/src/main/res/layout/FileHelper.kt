package com.example.suararakyatv2.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.suararakyatv2.R

object FileHelper {
    fun handleEvidence(context: Context, fileUrl: String?, imageView: ImageView) {
        if (fileUrl.isNullOrEmpty()) {
            imageView.visibility = View.GONE
            return
        }
        imageView.visibility = View.VISIBLE
        imageView.setOnClickListener(null)

        val uri = Uri.parse(fileUrl)
        when {
            fileUrl.contains(".jpg", true) || fileUrl.contains(".png", true) || fileUrl.contains(".jpeg", true) -> {
                Glide.with(context).load(fileUrl).into(imageView)
                setIntent(context, imageView, uri, "image/*", context.getString(R.string.view_image))
            }
            fileUrl.contains(".pdf", true) -> {
                imageView.setImageResource(R.drawable.ic_pdf_placeholder)
                setIntent(context, imageView, uri, "application/pdf", context.getString(R.string.open_pdf))
            }
            fileUrl.contains(".mp4", true) -> {
                Glide.with(context).load(fileUrl).placeholder(R.drawable.ic_video_placeholder).into(imageView)
                setIntent(context, imageView, uri, "video/mp4", context.getString(R.string.play_video))
            }
            else -> {
                imageView.setImageResource(R.drawable.ic_file_placeholder)
                setIntent(context, imageView, uri, "*/*", context.getString(R.string.open_file))
            }
        }
    }

    private fun setIntent(context: Context, view: ImageView, uri: Uri, mimeType: String, title: String) {
        view.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, title))
        }
    }
}