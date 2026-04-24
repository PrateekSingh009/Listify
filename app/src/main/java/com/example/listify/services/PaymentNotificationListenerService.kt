package com.example.listify.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.listify.domain.model.DetectedPayment
import com.example.listify.domain.repository.DataRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PaymentNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var repository: DataRepository

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val notification = sbn.notification
        val extras = notification.extras

        val title = extras.getString("android.title") ?: ""
        val text = extras.getString("android.text") ?: ""

        // Only process notifications from major payment apps
        if (!isPaymentApp(packageName)) return

        parseAndSavePayment(packageName, title, text)
    }

    private fun isPaymentApp(packageName: String): Boolean {
        return packageName in listOf(
            "com.android.shell",          // Testing
            "com.phonepe.app",           // PhonePe
            "com.google.android.apps.nbu.paisa.user", // Google Pay
            "com.paytm.app",             // Paytm
            "in.org.npci.upi",           // BHIM
            "com.whatsapp"               // WhatsApp UPI (optional)
        )
    }

    private fun parseAndSavePayment(packageName: String, title: String, text: String) {
        val amount = extractAmount(text) ?: return
        val merchant = extractMerchant(text, title) ?: "Unknown Merchant"

        val detectedPayment = DetectedPayment(
            amount = amount,
            merchant = merchant,
            appName = getAppDisplayName(packageName),
            timestamp = System.currentTimeMillis(),
            rawText = text
        )

        scope.launch {
            repository.saveDetectedPayment(detectedPayment)
            Log.d("PaymentListener", "Detected: ₹$amount to $merchant via $packageName")
        }
    }

    // Simple but effective regex-based parsers (you can improve these later)
    private fun extractAmount(text: String): Double? {
        val regex = Regex("""(?:₹|Rs\.?|INR)\s*(\d{1,3}(?:,\d{3})*(?:\.\d{2})?)""")
        return regex.find(text)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()
    }

    private fun extractMerchant(text: String, title: String): String? {
        // Common patterns
        val merchantRegex = Regex("""(?:to|paid to|sent to)\s+([A-Za-z0-9\s&]+?)(?:\s+via|\s+UPI|$)""", RegexOption.IGNORE_CASE)
        return merchantRegex.find(text)?.groupValues?.get(1)?.trim()
            ?: title.takeIf { it.isNotBlank() && it.length < 30 }
    }

    private fun getAppDisplayName(packageName: String): String {
        return when (packageName) {
            "com.phonepe.app" -> "PhonePe"
            "com.google.android.apps.nbu.paisa.user" -> "Google Pay"
            "com.paytm.app" -> "Paytm"
            else -> "UPI App"
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Optional: You can clean up if needed
    }
}