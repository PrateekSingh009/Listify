package com.example.listify.presentation.screens.composables.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.listify.domain.model.ClusterWithCategories
import com.example.listify.domain.model.HomeScreenData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCategorySheet(
    homeScreenData: HomeScreenData,
    onCategorySelected: (Long) -> Unit,     // categoryId
    onDismiss: () -> Unit
) {
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
                text = "Add to which category?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1C1E)
            )
            Spacer(Modifier.height(20.dp))

            // Clusters
            homeScreenData.clusteredSections.forEach { section ->
                Text(
                    text = section.cluster.name.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(section.categories) { category ->
                        ListItem(
                            headlineContent = { Text(category.title) },
                            leadingContent = { Icon(Icons.Rounded.AccountTree, null, tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier.clickable { onCategorySelected(category.id) }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // General Categories
            if (homeScreenData.generalCategories.isNotEmpty()) {
                Text(
                    text = "GENERAL",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                    items(homeScreenData.generalCategories) { category ->
                        ListItem(
                            headlineContent = { Text(category.title) },
                            leadingContent = { Icon(Icons.Rounded.Category, null, tint = MaterialTheme.colorScheme.secondary) },
                            modifier = Modifier.clickable { onCategorySelected(category.id) }
                        )
                    }
                }
            }
        }
    }
}