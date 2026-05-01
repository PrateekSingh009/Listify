package com.example.listify.presentation.screens.composables.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.listify.presentation.screens.composables.ChoiceCard

@Composable
fun ChoosingSheet(
    title : String,
    subtitle : String,
    firstChoiceTitle : String,
    firstChoiceSubtitle : String,
    secondChoiceTitle : String,
    secondChoiceSubtitle : String,
    firstIcon : ImageVector,
    secondIcon : ImageVector,
    selectedChoice: Int = 0,
    onChooseFirst: () -> Unit,
    onChooseSecond: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1A1C1E)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ChoiceCard(
                modifier = Modifier.weight(1f),
                icon = {
                    Icon(
                        firstIcon,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                title = firstChoiceTitle,
                subtitle = firstChoiceSubtitle,
                isSelected = selectedChoice == 1,
                onClick = onChooseFirst
            )
            ChoiceCard(
                modifier = Modifier.weight(1f),
                icon = {
                    Icon(
                        secondIcon,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                title = secondChoiceTitle,
                subtitle = secondChoiceSubtitle,
                isSelected = selectedChoice == 2,
                onClick = onChooseSecond
            )
        }
    }
}