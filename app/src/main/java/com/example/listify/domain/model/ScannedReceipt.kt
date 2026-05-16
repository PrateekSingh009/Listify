package com.example.listify.domain.model

data class ScannedReceipt(
    val merchantName: String,
    val date: String,
    val totalAmount: Double,
    val items: List<ReceiptItem>
)

data class ReceiptItem(
    val name: String,
    val price: Double
)