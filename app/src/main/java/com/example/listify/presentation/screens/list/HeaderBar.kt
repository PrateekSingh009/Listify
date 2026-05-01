package com.example.listify.presentation.screens.list


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.listify.presentation.screens.utils.AddSheetState
import com.example.listify.presentation.screens.utils.extensions.toHeadlineCase

// ── Gradient matching the teal theme ─────────────────────────────────────
private val ProgressGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF1B5548), Color(0xFF2D8C76))
)
private val OverBudgetGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFD32F2F), Color(0xFFEF5350))
)

@Composable
fun HeaderBar(
    title: String,
    totalSpend: Double,
    todaySpend: Double,
    totalPlanned: Double,
    onAddExpenseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasBudget = totalPlanned > 0.0
    val isOverBudget = hasBudget && totalSpend > totalPlanned
    val progress = if (hasBudget) (totalSpend / totalPlanned).toFloat().coerceIn(0f, 1f) else 0f
    val remaining = totalPlanned - totalSpend

    Card(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF2EDE4))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
        Text(
            text = title.toHeadlineCase(),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "Total Spent",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF6B7280)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "₹${"%.2f".format(totalSpend)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1C1E)
                )
            }

            // TODAY chip
            if (todaySpend > 0) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = Color(0xFF0DC405),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "TODAY",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "₹${todaySpend.toInt()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E)
                        )
                    }
                }
            }
        }

        if (hasBudget) {
            Spacer(Modifier.height(20.dp))

            HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Planned Budget",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1C1E)
                )
                Text(
                    text = "₹${"%.2f".format(totalPlanned)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isOverBudget) Color(0xFFD32F2F) else Color(0xFF1A1C1E)
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Limit",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9CA3AF)
                )
                Text(
                    text = "Spent",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9CA3AF)
                )
            }

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFE5E7EB))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            brush = if (isOverBudget) OverBudgetGradient else ProgressGradient
                        )
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "₹${totalSpend.toInt()} Spent",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = if (isOverBudget)
                        "₹${"%.2f".format(Math.abs(remaining))} Over Limit"
                    else
                        "₹${"%.2f".format(remaining)} Remaining",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isOverBudget) Color(0xFFD32F2F) else Color(0xFF6B7280)
                )
            }
            Spacer(Modifier.height(12.dp))

            HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onAddExpenseClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "Add Expense",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(Modifier.width(12.dp))
            Surface(
                color = Color.White.copy(alpha = 0.20f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
    }
}