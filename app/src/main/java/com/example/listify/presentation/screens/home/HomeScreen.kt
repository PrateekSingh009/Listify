package com.example.listify.presentation.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.AddCard
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.listify.data.local.dao.DetectedPaymentDao
import com.example.listify.domain.model.Category
import com.example.listify.domain.model.Cluster
import com.example.listify.domain.model.ClusterWithCategories
import com.example.listify.domain.model.DetectedPayment
import com.example.listify.domain.model.HomeScreenData
import com.example.listify.presentation.screens.composables.sheets.AddClusterSheet
import com.example.listify.presentation.screens.composables.sheets.ChoosingSheet
import com.example.listify.presentation.screens.composables.HeaderStat
import com.example.listify.presentation.screens.composables.PageEmptyState
import com.example.listify.presentation.screens.composables.sheets.SelectCategorySheet
import com.example.listify.presentation.screens.composables.utils.PointedDivider
import com.example.listify.presentation.screens.composables.sheets.SingleFieldSheet
import com.example.listify.presentation.screens.utils.AddSheetState

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onListItemClick: (Long) -> Unit,
    onNotificationClick: () -> Unit
) {
    val homeScreenData by homeViewModel.homeScreenData.collectAsState()

    val unprocessedPayments by homeViewModel.unprocessedDetectedPayments.collectAsState(initial = emptyList())
    val ignoredIds by homeViewModel.ignoredPaymentIds.collectAsState()

    HomeScreenContent(
        data = homeScreenData,
        unprocessedPayments = unprocessedPayments,
        ignoredIds = ignoredIds,
        onClick = onListItemClick,
        onNotificationClick = onNotificationClick,
        onAddCategory = homeViewModel::addCategory,
        onAddCluster = homeViewModel::addCluster,
        onAddDetectedPaymentToCategory = homeViewModel::addDetectedPaymentToCategory,
        onIgnoreDetectedPayment = homeViewModel::ignoreDetectedPayment
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    data: HomeScreenData,
    onClick: (Long) -> Unit,
    onNotificationClick : () -> Unit,
    onAddCategory: (String, Long?) -> Unit,
    onAddCluster: (String, String) -> Unit,
    unprocessedPayments: List<DetectedPayment>,
    ignoredIds: Set<Long>,
    onAddDetectedPaymentToCategory: (Long, Long, String, Double) -> Unit,
    onIgnoreDetectedPayment: (Long) -> Unit
) {
    var sheetState by remember { mutableStateOf<AddSheetState>(AddSheetState.Hidden) }
    var categoryTitle by remember { mutableStateOf("") }
    var clusterName by remember { mutableStateOf("") }
    var firstCategoryName by remember { mutableStateOf("") }

    var showCategorySelectionSheet by remember { mutableStateOf<DetectedPayment?>(null) }

    val visiblePayments = unprocessedPayments.filter { it.id !in ignoredIds }

    fun closeSheet() {
        sheetState = AddSheetState.Hidden
        categoryTitle = ""; clusterName = ""; firstCategoryName = ""
    }

    val totalCategories =
        data.clusteredSections.sumOf { it.categories.size } + data.generalCategories.size

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .statusBarsPadding()
        ) {
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
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
                        }
                        IconButton(onClick = { onNotificationClick() }) {
                            Icon(
                                imageVector = Icons.Rounded.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        IconButton(onClick = { /* TODO: Profile action */ }) {
                            Icon(
                                imageVector = Icons.Rounded.AccountCircle,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { sheetState = AddSheetState.Choosing },
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                if (visiblePayments.isNotEmpty()) {
                    item(key = "detected_payment") {
                        DetectedPaymentPrompt(
                            payment = visiblePayments.first(),
                            onAdd = { payment ->
                                showCategorySelectionSheet = payment
                            },
                            onIgnore = { payment ->
                                onIgnoreDetectedPayment(payment.id)
                            }
                        )
                    }
                }
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
                if (data.generalCategories.isNotEmpty()) {
                    item {
//                        SectionLabel("General")
                        PointedDivider(
                            modifier = Modifier.padding(
                                start = 32.dp,
                                end = 32.dp,
                                top = 24.dp,
                                bottom = 12.dp
                            ),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    items(data.generalCategories) { category ->
                        GeneralCategoryCard(category = category, onClick = onClick)
                    }
                }
                if (data.clusteredSections.isEmpty() && data.generalCategories.isEmpty()) {
                    item { PageEmptyState() }
                }
            }
        }
    }

    if (showCategorySelectionSheet != null) {
        SelectCategorySheet(
            detectedPayment = showCategorySelectionSheet!!,
            homeScreenData = data,
            onConfirm = { categoryId, finalTitle, finalAmount ->
                onAddDetectedPaymentToCategory(
                    showCategorySelectionSheet!!.id,
                    categoryId,
                    finalTitle,
                    finalAmount
                )
                showCategorySelectionSheet = null
            },
            onDismiss = { showCategorySelectionSheet = null }
        )
    }

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
                        title = "What would you like to add?",
                        subtitle = "Create a standalone list or a named group of lists.",
                        firstChoiceTitle = "Category",
                        firstChoiceSubtitle = "A single list",
                        secondChoiceTitle = "Cluster",
                        secondChoiceSubtitle = "Group of lists",
                        firstIcon = Icons.Rounded.Category,
                        secondIcon = Icons.Rounded.AccountTree,
                        selectedChoice = 0,
                        onChooseFirst = { sheetState = AddSheetState.AddingGeneral },
                        onChooseSecond = { sheetState = AddSheetState.AddingCluster }
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
//
//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    val data = HomeScreenData(
//        clusteredSections = listOf(
//            ClusterWithCategories(
//                cluster = Cluster(1, "Monthly Expense", System.currentTimeMillis()),
//                categories = listOf(
//                    Category(1, 1, "Apr", System.currentTimeMillis()),
//                    Category(2, 1, "Mar", System.currentTimeMillis()),
//                    Category(3, 1, "Feb", System.currentTimeMillis()),
//                )
//            ),
//            ClusterWithCategories(
//                cluster = Cluster(2, "Trip", System.currentTimeMillis()),
//                categories = listOf(
//                    Category(4, 2, "Noida", System.currentTimeMillis()),
//                    Category(5, 2, "Shimla", System.currentTimeMillis()),
//                )
//            )
//        ),
//        generalCategories = listOf(
//            Category(6, null, "Grocery", System.currentTimeMillis()),
//            Category(7, null, "Petrol", System.currentTimeMillis()),
//            Category(8, null, "Shopping", System.currentTimeMillis()),
//        )
//    )
//    HomeScreenContent(data = data, onClick = {}, onAddCategory = { _, _ -> }, onAddCluster = { _, _ -> })
//}