package com.example.listify.presentation.screens.composables.sheets

import androidx.compose.runtime.Composable

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