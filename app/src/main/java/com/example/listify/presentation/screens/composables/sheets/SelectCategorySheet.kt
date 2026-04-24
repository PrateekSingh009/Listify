package com.example.listify.presentation.screens.composables.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.listify.domain.model.HomeScreenData
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.listify.presentation.screens.utils.extensions.toHeadlineCase

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SelectCategorySheet(
    detectedPayment: com.example.listify.domain.model.DetectedPayment,
    homeScreenData: HomeScreenData,
    onConfirm: (Long, String, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var editableTitle by remember { mutableStateOf(detectedPayment.merchant) }
    var editableAmount by remember { mutableStateOf(detectedPayment.amount.toString()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 8.dp,
                bottom = 48.dp
            )
        ) {
            item(key = "sheet_title") {
                Text(
                    text = if (selectedCategoryId == null) "Select Category" else "Verify Details",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1C1E)
                )
                Spacer(Modifier.height(16.dp))
            }
            itemsIndexed(
                items = homeScreenData.clusteredSections,
                key = { _, section -> "cluster_${section.cluster.id}" }
            ) { _, section ->
                CategoryGroup(
                    title = section.cluster.name.uppercase(),
                    categories = section.categories,
                    selectedId = selectedCategoryId,
                    onCategorySelected = { selectedCategoryId = it }
                )
                Spacer(Modifier.height(16.dp))
            }

            if (homeScreenData.generalCategories.isNotEmpty()) {
                item(key = "general_group") {
                    CategoryGroup(
                        title = "GENERAL",
                        categories = homeScreenData.generalCategories,
                        selectedId = selectedCategoryId,
                        onCategorySelected = { selectedCategoryId = it }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
            item(key = "edit_fields") {
                AnimatedVisibility(
                    visible = selectedCategoryId != null,
                    enter = expandVertically(
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + fadeIn(tween(220)),
                    exit = shrinkVertically(
                        animationSpec = tween(250)
                    ) + fadeOut(tween(180))
                ) {
                    Column {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                        OutlinedTextField(
                            value = editableTitle,
                            onValueChange = { editableTitle = it },
                            label = { Text("Transaction Title") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = editableAmount,
                            onValueChange = { editableAmount = it },
                            label = { Text("Amount") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            prefix = { Text("₹") }
                        )

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val finalAmount = editableAmount.toDoubleOrNull() ?: 0.0
                                selectedCategoryId?.let {
                                    onConfirm(it, editableTitle, finalAmount)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = editableTitle.isNotBlank() &&
                                    (editableAmount.toDoubleOrNull() ?: 0.0) > 0
                        ) {
                            Text("Add Transaction", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryGroup(
    title: String,
    categories: List<com.example.listify.domain.model.Category>,
    selectedId: Long?,
    onCategorySelected: (Long) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            letterSpacing = 1.2.sp
        )
        Spacer(Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachRow = Int.MAX_VALUE
        ) {
            categories.forEach { category ->
                val isSelected = category.id == selectedId
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category.id) },
                    label = {
                        Text(
                            text = category.title.toHeadlineCase(),
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Rounded.Check, null, Modifier.size(16.dp)) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFFF1F3F5),
                        labelColor = Color(0xFF495057),
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    border = null
                )
            }
        }
    }
}