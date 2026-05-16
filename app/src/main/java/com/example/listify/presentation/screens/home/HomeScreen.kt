package com.example.listify.presentation.screens.home

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.AddCard
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import com.example.listify.ui.theme.ListifyTypography

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onReceiptScannerClick : () -> Unit,
    onListItemClick: (Long) -> Unit,
    onNotificationClick: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                homeViewModel.refreshActivePrompt()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val homeScreenData by homeViewModel.homeScreenData.collectAsState()

    val activePrompt by homeViewModel.activePrompt.collectAsState()

    HomeScreenContent(
        data = homeScreenData,
        onClick = onListItemClick,
        activePrompt = activePrompt,
        onNotificationClick = onNotificationClick,
        onReceiptScannerClick  = onReceiptScannerClick,
        onAddCategory = homeViewModel::addCategory,
        onAddCluster = homeViewModel::addCluster,
        onIgnorePrompt = homeViewModel::ignorePrompt,
        onAddDetectedPaymentToCategory = homeViewModel::addDetectedPaymentToCategory
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    data: HomeScreenData,
    onClick: (Long) -> Unit,
    activePrompt: DetectedPayment?,
    onNotificationClick : () -> Unit,
    onReceiptScannerClick : () -> Unit,
    onAddCategory: (String, Long?) -> Unit,
    onAddCluster: (String, String) -> Unit,
    onIgnorePrompt: () -> Unit,
    onAddDetectedPaymentToCategory: (Long, Long, String, Double) -> Unit,
) {
    var sheetState by remember { mutableStateOf<AddSheetState>(AddSheetState.Hidden) }
    var categoryTitle by remember { mutableStateOf("") }
    var clusterName by remember { mutableStateOf("") }
    var firstCategoryName by remember { mutableStateOf("") }

    var showCategorySelectionSheet by remember { mutableStateOf<DetectedPayment?>(null) }

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
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter // Aligns children to the bottom center of the Box
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                    colors = cardColors(containerColor = colorScheme.primary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 40.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Listify",
                                    color = colorScheme.onPrimary,
                                    style = MaterialTheme.typography.displayMedium
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "Pencil it in",
                                    color = colorScheme.onPrimary.copy(alpha = 0.65f),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            IconButton(onClick = { onReceiptScannerClick() }) {
                                Icon(
                                    imageVector = Icons.Rounded.DocumentScanner,
                                    contentDescription = "Scan Document",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            IconButton(onClick = { onNotificationClick() }) {
                                Icon(
                                    imageVector = if (activePrompt != null) Icons.Rounded.NotificationsActive else Icons.Rounded.Notifications,
                                    contentDescription = "Notifications",
                                    tint = colorScheme.onPrimary
                                )
                            }
//                            IconButton(onClick = { /* TODO: Profile action */ }) {
//                                Icon(
//                                    imageVector = Icons.Rounded.AccountCircle,
//                                    contentDescription = "Profile",
//                                    tint = colorScheme.onPrimary
//                                )
//                            }
                        }
                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HeaderStat(
                                modifier = Modifier.weight(1f),
                                label = "Clusters",
                                value = data.clusteredSections.size.toString(),
                                icon = Icons.Outlined.Layers
                            )
                            HeaderStat(
                                modifier = Modifier.weight(1f),
                                label = "Categories",
                                value = totalCategories.toString(),
                                icon = Icons.Outlined.Category
                            )
                            HeaderStat(
                                modifier = Modifier.weight(1f),
                                label = "General",
                                value = data.generalCategories.size.toString(),
                                icon = Icons.Outlined.GridView
                            )
                        }
                    }
                }
                Button(
                    onClick = { sheetState = AddSheetState.Choosing },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(56.dp)
                        .offset(y = 28.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.onPrimary,
                        contentColor = colorScheme.primary
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
            }
            Spacer(modifier = Modifier.height(40.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                item(
                    "notification_message",
                    content = { NotificationPermissionSection() },
                )

                if (activePrompt != null) {
                    item(key = "prompt_${activePrompt.id}") {
                        DetectedPaymentPrompt(
                            payment = activePrompt,
                            onAdd = { showCategorySelectionSheet = it },
                            onIgnore = { onIgnorePrompt() }
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
                            color = colorScheme.outlineVariant
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

fun isNotificationServiceEnabled(context: Context): Boolean {
    val packageNames = NotificationManagerCompat.getEnabledListenerPackages(context)
    return packageNames.contains(context.packageName)
}

@Composable
fun NotificationPermissionSection() {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var isEnabled by remember {
        mutableStateOf(isNotificationServiceEnabled(context))
    }

    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                isEnabled = isNotificationServiceEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (!isEnabled) {
        Card(
            colors = cardColors(
                containerColor = colorScheme.errorContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    context.startActivity(intent)
                }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = colorScheme.error
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Automatic tracking is off. Tap to enable access.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onErrorContainer
                )
            }
        }
    }
}