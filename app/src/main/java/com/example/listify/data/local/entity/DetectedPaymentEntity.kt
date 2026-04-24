package com.example.listify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detected_payments")
data class DetectedPaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val merchant: String,
    val appName: String,
    val timestamp: Long,
    val rawText: String,
    val isProcessed: Boolean = false
)