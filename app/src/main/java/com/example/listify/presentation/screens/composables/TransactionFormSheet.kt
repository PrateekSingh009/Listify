package com.example.listify.presentation.screens.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.listify.domain.model.Transaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormSheet(
    isEditMode: Boolean = false,
    initialTransaction: Transaction? = null,   // only used in edit mode
    uniqueTitles: List<String>,
    onSave: (String, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(initialTransaction?.title ?: "") }
    var amountText by remember { mutableStateOf(initialTransaction?.amount?.toString() ?: "") }
    var selectedExistingTitle by remember { mutableStateOf<String?>(null) }

    val isExistingSelected = selectedExistingTitle != null
    val finalTitle = selectedExistingTitle ?: title

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp, top = 8.dp)
        ) {
            Text(
                text = if (isEditMode) "Edit Transaction" else "Add Transaction",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1C1E)
            )
            Spacer(Modifier.height(20.dp))

            // Amount Field (first - consistent with your preference)
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount (₹)") },
                placeholder = { Text("0") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(12.dp))

            // Title Field with Smart Autocomplete
            OutlinedTextField(
                value = if (isExistingSelected) selectedExistingTitle!! else title,
                onValueChange = {
                    if (!isExistingSelected) title = it
                },
                label = { Text("Title") },
                placeholder = { Text("What did you spend on?") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                enabled = !isExistingSelected,
                trailingIcon = {
                    if (isExistingSelected) {
                        IconButton(onClick = { selectedExistingTitle = null }) {
                            Icon(Icons.Filled.Close, "Clear", tint = Color.Gray)
                        }
                    }
                }
            )

            // Suggestions (shown while typing)
            if (!isExistingSelected && title.isNotEmpty()) {
                val filtered = uniqueTitles.filter { it.contains(title, ignoreCase = true) }
                if (filtered.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text("Previous titles", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))

                    LazyColumn(modifier = Modifier.heightIn(max = 220.dp)) {
                        items(filtered) { suggestion ->
                            ListItem(
                                headlineContent = { Text(suggestion) },
                                modifier = Modifier.clickable {
                                    selectedExistingTitle = suggestion
                                    title = ""
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (finalTitle.isNotBlank() && amount > 0) {
                        onSave(finalTitle, amount)
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = finalTitle.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text(
                    text = if (isEditMode) "Update Transaction" else "Add Transaction",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}