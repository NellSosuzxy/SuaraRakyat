package com.example.suararakyatv2

import java.util.Date

data class Report(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val location: String = "",
    val status: String = "",
    val timestamp: Date? = null,
    val imageUrl: String = "",
    val reporterId: String = ""
)
