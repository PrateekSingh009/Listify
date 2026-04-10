package com.example.listify.presentation.screens.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EditCategorySheet(
    currentTitle: String,
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    SingleFieldSheet(
        title = "Edit Category",
        subtitle = "Update the name of \"$currentTitle\"",
        fieldLabel = "Category Name",
        hint = "e.g. Grocery, Trip Expenses...",
        value = value,
        onValueChange = onValueChange,
        confirmLabel = "Save Changes",
        onConfirm = onConfirm
    )
}