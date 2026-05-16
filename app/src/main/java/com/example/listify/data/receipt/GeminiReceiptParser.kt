package com.example.listify.data.receipt

import android.content.Context
import android.util.Log
import com.example.listify.domain.model.ReceiptItem
import com.example.listify.domain.model.ScannedReceipt
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.generationConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.getValue

private const val TAG = "GeminiReceiptParser"

private fun buildPrompt(rawOcrText: String): String = """
You are a receipt parser. Below is raw OCR text extracted from a receipt.

Extract ONLY the following fields and respond with a valid JSON object — no markdown, no backticks, no explanation.

JSON format:
{
  "merchantName": "string",
  "date": "string (dd/MM/yyyy or empty if not found)",
  "totalAmount": number,
  "items": [
    { "name": "string", "price": number }
  ]
}

Rules:
- totalAmount must be a number (e.g. 450.00), not a string.
- If a field cannot be found, use empty string or 0.
- Items list can be empty if no line items are found.
- Do NOT include currency symbols in numbers.

Raw OCR Text:
$rawOcrText
""".trimIndent()

@Singleton
class GeminiReceiptParser @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val model by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-2.5-flash",
            generationConfig = generationConfig {
                temperature = 0.1f          // Low temp = deterministic JSON
                maxOutputTokens = 4096
                responseMimeType = "application/json"
            }
        )
    }

    suspend fun parseReceiptText(rawOcrText: String): ScannedReceipt {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = buildPrompt(rawOcrText)
                val response = model.generateContent(prompt)
                val jsonText = response.text
                    ?: throw IllegalStateException("Firebase AI returned empty response")

                Log.d(TAG, "Firebase AI response: $jsonText")
                parseJsonToReceipt(jsonText)
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI failed: ${e.message}")
                throw e
            }
        }
    }

    private fun parseJsonToReceipt(rawJson: String): ScannedReceipt {
        val cleaned = rawJson
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val json = JSONObject(cleaned)

        val items = mutableListOf<ReceiptItem>()
        val itemsArray: JSONArray? = json.optJSONArray("items")
        if (itemsArray != null) {
            for (i in 0 until itemsArray.length()) {
                val item = itemsArray.getJSONObject(i)
                items.add(
                    ReceiptItem(
                        name = item.optString("name", "Unknown Item"),
                        price = item.optDouble("price", 0.0)
                    )
                )
            }
        }

        return ScannedReceipt(
            merchantName = json.optString("merchantName", "Unknown Merchant"),
            date = json.optString("date", ""),
            totalAmount = json.optDouble("totalAmount", 0.0),
            items = items
        )
    }
}