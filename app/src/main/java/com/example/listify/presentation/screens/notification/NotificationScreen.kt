package com.example.listify.presentation.screens.notification

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.listify.domain.model.DetectedPayment
import com.example.listify.domain.model.HomeScreenData
import com.example.listify.presentation.screens.composables.AutoDismissInfoBanner
import com.example.listify.presentation.screens.composables.sheets.SelectCategorySheet
import java.util.concurrent.TimeUnit

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
        AutoDismissInfoBanner(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            "These payments will be removed automatically in 7 days.",
            5
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
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

                    val daysRemaining = remember(payment.timestamp) {
                        val diff = System.currentTimeMillis() - payment.timestamp
                        val daysPassed = TimeUnit.MILLISECONDS.toDays(diff)
                        7 - daysPassed
                    }

                    DetectedPaymentCard(
                        payment = payment,
                        isProcessed = false,
                        daysRemaining = daysRemaining.toInt(),
                        onAddClick = { showSelectSheet = payment }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
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
                        daysRemaining = 0,
                        onAddClick = {}
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
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