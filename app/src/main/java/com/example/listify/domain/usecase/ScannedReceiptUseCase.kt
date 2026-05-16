package com.example.listify.domain.usecase

import android.graphics.Bitmap
import com.example.listify.data.receipt.GeminiReceiptParser
import com.example.listify.data.receipt.OcrEngine
import com.example.listify.domain.model.ScannedReceipt
import javax.inject.Inject

class ScannedReceiptUseCase @Inject constructor(
    private val ocrEngine: OcrEngine,
    private val geminiParser: GeminiReceiptParser
) {
    suspend operator fun invoke(bitmap: Bitmap): Result<ScannedReceipt> = runCatching {
        val rawText = ocrEngine.extractText(bitmap)
        if (rawText.isBlank()) {
            throw IllegalStateException("No text found in image. Please try a clearer photo.")
        }
        geminiParser.parseReceiptText(rawText)
    }
}