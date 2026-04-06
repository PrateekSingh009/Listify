package com.example.listify.presentation.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCard
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.listify.domain.model.Category
import com.example.listify.domain.model.Cluster
import com.example.listify.domain.model.ClusterWithCategories
import com.example.listify.domain.model.HomeScreenData
import com.example.listify.presentation.screens.composables.AddClusterSheet
import com.example.listify.presentation.screens.composables.HeaderStat
import com.example.listify.presentation.screens.composables.PointedDivider
import com.example.listify.presentation.screens.composables.SingleFieldSheet
import com.example.listify.presentation.screens.utils.AddSheetState

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onClick: (Long) -> Unit,
) {
    val homeScreenData by homeViewModel.homeScreenData.collectAsState()

    HomeScreenContent(
        data = homeScreenData,
        onClick = onClick,
        onAddCategory = homeViewModel::addCategory,
        onAddCluster = homeViewModel::addCluster
    )
}

// ── Root content ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    data: HomeScreenData,
    onClick: (Long) -> Unit,
    onAddCategory: (String, Long?) -> Unit,
    onAddCluster: (String, String) -> Unit,
) {
    var sheetState by remember { mutableStateOf<AddSheetState>(AddSheetState.Hidden) }
    var categoryTitle by remember { mutableStateOf("") }
    var clusterName by remember { mutableStateOf("") }
    var firstCategoryName by remember { mutableStateOf("") }

    fun closeSheet() {
        sheetState = AddSheetState.Hidden
        categoryTitle = ""; clusterName = ""; firstCategoryName = ""
    }

    val totalCategories = data.clusteredSections.sumOf { it.categories.size } + data.generalCategories.size

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .statusBarsPadding()
        ) {
            // ── Header ─────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    Text(
                        text = "Listify",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.65f),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Pencil it in",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { sheetState = AddSheetState.Choosing } ,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddCard,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Add to the Book",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HeaderStat(
                            modifier = Modifier.weight(1f),
                            label = "Clusters",
                            value = data.clusteredSections.size.toString()
                        )
                        HeaderStat(
                            modifier = Modifier.weight(1f),
                            label = "Categories",
                            value = totalCategories.toString()
                        )
                        HeaderStat(
                            modifier = Modifier.weight(1f),
                            label = "General",
                            value = data.generalCategories.size.toString()
                        )
                    }
                }
            }

            // ── Body ───────────────────────────────────────────────────
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // Clustered sections
                items(data.clusteredSections) { section ->
                    ClusterSection(
                        section = section,
                        onCategoryClick = onClick,
                        onAddToCluster = {
                            sheetState = AddSheetState.AddingToCluster(
                                clusterId = section.cluster.id,
                                clusterName = section.cluster.name
                            )
                        }
                    )
                }

                // General section
                if (data.generalCategories.isNotEmpty()) {
                    item {
//                        SectionLabel("General")
                        PointedDivider(
                            modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    items(data.generalCategories) { category ->
                        GeneralCategoryCard(category = category, onClick = onClick)
                    }
                }

                // Empty state
                if (data.clusteredSections.isEmpty() && data.generalCategories.isEmpty()) {
                    item { EmptyState() }
                }
            }
        }
    }

    // ── Bottom Sheet ───────────────────────────────────────────────
    if (sheetState !is AddSheetState.Hidden) {
        ModalBottomSheet(
            onDismissRequest = { closeSheet() },
            containerColor = Color.White,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            AnimatedContent(
                targetState = sheetState,
                transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(180)) },
                label = "SheetTransition"
            ) { state ->
                when (state) {
                    is AddSheetState.Choosing -> ChoosingSheet(
                        onChooseCategory = { sheetState = AddSheetState.AddingGeneral },
                        onChooseCluster = { sheetState = AddSheetState.AddingCluster }
                    )

                    is AddSheetState.AddingGeneral -> SingleFieldSheet(
                        title = "New Category",
                        subtitle = "It will appear in the General section.",
                        fieldLabel = "Category Name",
                        hint = "e.g. Grocery, Gym, Travel…",
                        value = categoryTitle,
                        onValueChange = { categoryTitle = it },
                        confirmLabel = "Add Category",
                        onConfirm = {
                            onAddCategory(categoryTitle, null)
                            closeSheet()
                        }
                    )

                    is AddSheetState.AddingCluster -> AddClusterSheet(
                        clusterName = clusterName,
                        firstCategory = firstCategoryName,
                        onClusterNameChange = { clusterName = it },
                        onFirstCategoryChange = { firstCategoryName = it },
                        onConfirm = {
                            onAddCluster(clusterName, firstCategoryName)
                            closeSheet()
                        }
                    )

                    is AddSheetState.AddingToCluster -> SingleFieldSheet(
                        title = "Add to \"${state.clusterName}\"",
                        subtitle = "The new category will be added to this cluster.",
                        fieldLabel = "Category Name",
                        hint = "New category name…",
                        value = categoryTitle,
                        onValueChange = { categoryTitle = it },
                        confirmLabel = "Add",
                        onConfirm = {
                            onAddCategory(categoryTitle, state.clusterId)
                            closeSheet()
                        }
                    )

                    else -> {}
                }
            }
        }
    }
}

// ── Cluster section ────────────────────────────────────────────────────────

@Composable
fun ClusterSection(
    section: ClusterWithCategories,
    onCategoryClick: (Long) -> Unit,
    onAddToCluster: () -> Unit,
) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = section.cluster.name.uppercase(),
            modifier = Modifier.padding(start = 20.dp, bottom = 10.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = androidx.compose.ui.unit.TextUnit(1.2f, androidx.compose.ui.unit.TextUnitType.Sp)
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

@Composable
fun ClusterCategoryCard(category: Category, onClick: (Long) -> Unit) {
    Card(
        onClick = { onClick(category.id) },
        modifier = Modifier.size(width = 86.dp, height = 90.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    modifier = Modifier.padding(9.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.height(7.dp))
            Text(
                text = category.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF1A1C1E)
            )
        }
    }
}

@Composable
fun AddToClusterCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(width = 86.dp, height = 90.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add to cluster",
                    modifier = Modifier.padding(7.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

// ── General section ────────────────────────────────────────────────────────

@Composable
fun SectionLabel(title: String) {
    Text(
        text = title.uppercase(),
        modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        letterSpacing = androidx.compose.ui.unit.TextUnit(1.2f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
}

@Composable
fun GeneralCategoryCard(category: Category, onClick: (Long) -> Unit) {
    Card(
        onClick = { onClick(category.id) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = category.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}
// ── Empty state ────────────────────────────────────────────────────────────

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Nothing here yet", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text(
                "Tap + to create a category or cluster",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
        }
    }
}

// ── Sheet composables ──────────────────────────────────────────────────────

@Composable
fun ChoosingSheet(onChooseCategory: () -> Unit, onChooseCluster: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What would you like to add?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1A1C1E)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Create a standalone list or a named group of lists.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ChoiceCard(
                modifier = Modifier.weight(1f),
                icon = { Icon(Icons.Rounded.Category, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp)) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                title = "Category",
                subtitle = "A single list",
                onClick = onChooseCategory
            )
            ChoiceCard(
                modifier = Modifier.weight(1f),
                icon = { Icon(Icons.Rounded.AccountTree, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(28.dp)) },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                title = "Cluster",
                subtitle = "Group of lists",
                onClick = onChooseCluster
            )
        }
    }
}

@Composable
fun ChoiceCard(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    containerColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            icon()
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

// ── Preview ────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val data = HomeScreenData(
        clusteredSections = listOf(
            ClusterWithCategories(
                cluster = Cluster(1, "Monthly Expense", System.currentTimeMillis()),
                categories = listOf(
                    Category(1, 1, "Apr", System.currentTimeMillis()),
                    Category(2, 1, "Mar", System.currentTimeMillis()),
                    Category(3, 1, "Feb", System.currentTimeMillis()),
                )
            ),
            ClusterWithCategories(
                cluster = Cluster(2, "Trip", System.currentTimeMillis()),
                categories = listOf(
                    Category(4, 2, "Noida", System.currentTimeMillis()),
                    Category(5, 2, "Shimla", System.currentTimeMillis()),
                )
            )
        ),
        generalCategories = listOf(
            Category(6, null, "Grocery", System.currentTimeMillis()),
            Category(7, null, "Petrol", System.currentTimeMillis()),
            Category(8, null, "Shopping", System.currentTimeMillis()),
        )
    )
    HomeScreenContent(data = data, onClick = {}, onAddCategory = { _, _ -> }, onAddCluster = { _, _ -> })
}