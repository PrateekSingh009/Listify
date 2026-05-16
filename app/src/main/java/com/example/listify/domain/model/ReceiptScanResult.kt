package com.example.listify.domain.model

sealed class ReceiptScanResult {
    object Idle : ReceiptScanResult()
    object Scanning : ReceiptScanResult()
    data class Success(val receipt: ScannedReceipt) : ReceiptScanResult()
    data class Error(val message: String) : ReceiptScanResult()
}