package com.example.listify.presentation.screens.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun AddClusterSheet(
    clusterName: String,
    firstCategory: String,
    onClusterNameChange: (String) -> Unit,
    onFirstCategoryChange: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp, top = 8.dp)
    ) {
        Text("New Cluster", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1C1E))
        Spacer(Modifier.height(4.dp))
        Text("Groups multiple related categories together.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = clusterName,
            onValueChange = onClusterNameChange,
            label = { Text("Cluster Name") },
            placeholder = { Text("e.g. Monthly Expense, Trip…") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = firstCategory,
            onValueChange = onFirstCategoryChange,
            label = { Text("First Category") },
            placeholder = { Text("e.g. January, Goa Trip…") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onConfirm,
            enabled = clusterName.isNotBlank() && firstCategory.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Create Cluster", fontWeight = FontWeight.Bold)
        }
    }
}