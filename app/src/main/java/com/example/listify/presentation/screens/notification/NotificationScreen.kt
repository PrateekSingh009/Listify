package com.example.listify.presentation.screens.notification

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.listify.domain.model.DetectedPayment
import com.example.listify.domain.model.HomeScreenData
import com.example.listify.domain.utils.formatTimestamp
import com.example.listify.presentation.screens.composables.sheets.SelectCategorySheet

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val unprocessed by viewModel.unprocessedPayments.collectAsState()
    val processed by viewModel.processedPayments.collectAsState()
    val homeScreenData by viewModel.homeScreenData.collectAsState()

    NotificationScreenContent(
        unprocessedPayments = unprocessed,
        processedPayments = processed,
        homeScreenData = homeScreenData,
        onBackClick = onBackClick,
        onAddPayment = viewModel::addPaymentToCategory
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreenContent(
    unprocessedPayments: List<DetectedPayment>,
    processedPayments: List<DetectedPayment>,
    homeScreenData: HomeScreenData,
    onBackClick: () -> Unit,
    onAddPayment: (DetectedPayment, Long, String, Double) -> Unit
) {
    var showSelectSheet by remember { mutableStateOf<DetectedPayment?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
    ) {
        // ── Header ─────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Detected Payments",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${unprocessedPayments.size} pending · ${processedPayments.size} added",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.65f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // ── Body ───────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // ── Pending section ──────────────────────────────────
            if (unprocessedPayments.isNotEmpty()) {
                item(key = "pending_header") {
                    SectionHeader(
                        title = "Pending",
                        count = unprocessedPayments.size,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                }
                items(
                    items = unprocessedPayments,
                    key = { "pending_${it.id}" }
                ) { payment ->
                    DetectedPaymentCard(
                        payment = payment,
                        isProcessed = false,
                        onAddClick = { showSelectSheet = payment }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            // ── Divider ──────────────────────────────────────────
            if (unprocessedPayments.isNotEmpty() && processedPayments.isNotEmpty()) {
                item(key = "divider") {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                        Text(
                            text = "Added to Listify",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            // ── Processed section ────────────────────────────────
            if (processedPayments.isNotEmpty()) {
                if (unprocessedPayments.isEmpty()) {
                    item(key = "processed_header") {
                        SectionHeader(
                            title = "Added to Listify",
                            count = processedPayments.size,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
                items(
                    items = processedPayments,
                    key = { "processed_${it.id}" }
                ) { payment ->
                    DetectedPaymentCard(
                        payment = payment,
                        isProcessed = true,
                        onAddClick = {}
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            // ── Empty state ──────────────────────────────────────
            if (unprocessedPayments.isEmpty() && processedPayments.isEmpty()) {
                item(key = "empty") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "No payments detected yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Payments from PhonePe, GPay & Paytm\nwill appear here automatically",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }

    // ── SelectCategorySheet ─────────────────────────────────────────
    showSelectSheet?.let { payment ->
        SelectCategorySheet(
            detectedPayment = payment,
            homeScreenData = homeScreenData,
            onConfirm = { categoryId, title, amount ->
                onAddPayment(payment, categoryId, title, amount)
                showSelectSheet = null
            },
            onDismiss = { showSelectSheet = null }
        )
    }
}

// ── Section header chip ────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, count: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(Modifier.width(8.dp))
        Surface(
            color = color.copy(alpha = 0.12f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = count.toString(),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ── Individual payment card ────────────────────────────────────────

@Composable
private fun DetectedPaymentCard(
    payment: DetectedPayment,
    isProcessed: Boolean,
    onAddClick: () -> Unit
) {
    // Processed cards fade to 50% opacity — clean visual distinction
    val cardAlpha = if (isProcessed) 0.5f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isProcessed) Color(0xFFF0F0F0) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isProcessed) 0.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon placeholder
            Surface(
                color = if (isProcessed)
                    Color.Gray.copy(alpha = 0.15f)
                else
                    MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isProcessed)
                            Icons.Rounded.CheckCircle
                        else
                            Icons.Rounded.CreditCard,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = if (isProcessed)
                            Color.Gray
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Merchant + meta info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payment.merchant,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isProcessed) Color.Gray else Color(0xFF1A1C1E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    // Strikethrough on processed items
                    textDecoration = if (isProcessed)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = payment.appName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = formatTimestamp(payment.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )
            }

            Spacer(Modifier.width(8.dp))

            // Right side: amount + action
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isProcessed)
                        Color.Gray.copy(alpha = 0.12f)
                    else
                        Color(0xFF2C3E50)
                ) {
                    Text(
                        text = "₹${payment.amount.toInt()}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isProcessed) Color.Gray else Color.White
                    )
                }

                if (!isProcessed) {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onAddClick,
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Add",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}