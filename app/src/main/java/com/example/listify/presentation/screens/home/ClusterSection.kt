package com.example.listify.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.listify.domain.model.ClusterWithCategories

@Composable
fun ClusterSection(
    section: ClusterWithCategories,
    onCategoryClick: (Long) -> Unit,
    onAddToCluster: () -> Unit,
) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.padding(top = 18.dp)
    ) {
        // Uppercase teal cluster label — exactly like image
        Text(
            text = section.cluster.name.uppercase(),
            modifier = Modifier.padding(start = 20.dp, bottom = 10.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = TextUnit(1.6f, TextUnitType.Sp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(section.categories) { category ->
                ClusterCategoryCard(category = category, onClick = onCategoryClick)
            }
            item {
                AddToClusterCard(onClick = onAddToCluster)
            }
        }
    }
}

