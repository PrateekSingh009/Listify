package com.example.listify.presentation.screens.notification

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.listify.domain.model.DetectedPayment
import com.example.listify.domain.utils.formatTimestamp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetectedPaymentCard(
    payment: DetectedPayment,
    isProcessed: Boolean,
    daysRemaining: Int,
    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isDeleteModeActive: Boolean = false
) {
    val cardAlpha = if (isProcessed) 0.5f else 1f

    // Smooth fade animation for the overlay
    val overlayAlpha by animateFloatAsState(
        targetValue = if (isDeleteModeActive) 0.85f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "overlay_alpha"
    )

    val timerColor = when {
        isProcessed -> Color.Transparent
        daysRemaining <= 1 -> Color(0xFFE53935)
        daysRemaining <= 3 -> Color(0xFFFFA000)
        else -> Color(0xFF43A047)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(cardAlpha)
                .combinedClickable(
                    onClick = {  },
                    onLongClick = { onDeleteClick() }
                ),
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

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = payment.merchant,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isProcessed) Color.Gray else Color(0xFF1A1C1E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (isProcessed) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = payment.appName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(2.dp))
                    if (!isProcessed) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = timerColor
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (daysRemaining > 0) "Expires in $daysRemaining days" else "Expires today",
                                style = MaterialTheme.typography.labelSmall,
                                color = timerColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Text(
                            text = formatTimestamp(payment.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.LightGray
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

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

        if (overlayAlpha > 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Red.copy(alpha = overlayAlpha * 0.7f))
                    .alpha(overlayAlpha),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Release to delete",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}