package com.example.listify.domain.model

data class DetectedPayment(
    val id: Long = 0,
    val amount: Double,
    val merchant: String,
    val appName: String,
    val timestamp: Long,
    val rawText: String,
    val isProcessed: Boolean = false
)