package com.example.listify.presentation.screens.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ViewStream
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.listify.domain.model.Category
import com.example.listify.domain.model.Transaction
import com.example.listify.domain.utils.formatToDateAndTime
import com.example.listify.domain.utils.formatToDateOnly
import com.example.listify.domain.utils.formatToTimeOnly
import com.example.listify.presentation.screens.composables.AddClusterSheet
import com.example.listify.presentation.screens.composables.ChoosingSheet
import com.example.listify.presentation.screens.composables.DeleteConfirmationSheet
import com.example.listify.presentation.screens.composables.EditCategorySheet
import com.example.listify.presentation.screens.composables.HeaderStat
import com.example.listify.presentation.screens.composables.SingleFieldSheet
import com.example.listify.presentation.screens.composables.TotalExpenseSheet
import com.example.listify.presentation.screens.utils.AddSheetState
import com.example.listify.presentation.screens.utils.extensions.toHeadlineCase


enum class GroupMode { Date, Title }

data class TitleGroup(
    val title: String,
    val total: Double,
    val count: Int,
    val transactions: List<Transaction>
)

@Composable
fun ListScreen(
    listViewModel: ListViewModel = hiltViewModel(),
    onClick: () -> Unit
) {
    val expenses by listViewModel.transactions.collectAsState()
    val selectedGroup by listViewModel.selectedCategory.collectAsState()
    ListScreenContent(
        expenses = expenses,
        selectedGroup = selectedGroup,
        onBackClick = onClick,
        onDeleteCategory =  {
            onClick()
            listViewModel.deleteCategory(selectedGroup)
        },
        onAddTransaction = listViewModel::addTransaction,
        onSetTotalPlanned = listViewModel::setTotalPlanned,
        onClearTotalPlanned = listViewModel::clearTotalPlanned,
        onUpdateCategoryTitle = listViewModel::updateCategoryTitle,
        onDeleteTransaction = listViewModel::deleteTransaction
    )
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }

    // Smooth rotation for the arrow
    val arrowRotation by animateFloatAsState(
        targetValue = if (menuOpen) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "ArrowRotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ── Main Content ─────────────────────────────────────────
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(end = 64.dp) // Extra padding to avoid menu overlap
            ) {
                Text(
                    text = transaction.title.toHeadlineCase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(30.dp),
                    color = Color(0xFF2C3E50),
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = "₹${transaction.amount.toInt()}",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            // ── Integrated Pill Menu (Strictly Top End) ──────────────
            Surface(
                modifier = Modifier.align(Alignment.TopEnd),
                color = if (menuOpen) Color(0xFFF1F3F4) else Color.Transparent,
                shape = RoundedCornerShape(28.dp),
                shadowElevation = if (menuOpen) 3.dp else 0.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(2.dp)
                ) {
                    AnimatedVisibility(
                        visible = menuOpen,
                        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            IconButton(onClick = {
                                menuOpen = false
                                onEdit(transaction)
                            }, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = "Edit",
                                    tint = Color(0xFF1A1C1E),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(onClick = {
                                menuOpen = false
                                onDelete(transaction)
                            }, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // 4. Arrow Toggle (Always the "anchor" of the pill)
                    IconButton(
                        onClick = { menuOpen = !menuOpen },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Toggle Menu",
                            tint = Color(0xFF1A1C1E).copy(alpha = if (menuOpen) 0.8f else 0.3f),
                            modifier = Modifier
                                .size(12.dp)
                                .graphicsLayer { rotationZ = arrowRotation }
                        )
                    }
                }
            }

            // ── Timestamp (Bottom Start) ─────────────────────────────
            Text(
                text = formatToTimeOnly(transaction.updatedAt),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListScreenContent(
    expenses: List<Transaction>,
    selectedGroup: Category,
    onBackClick: () -> Unit,
    onDeleteCategory: () -> Unit,
    onAddTransaction: (String, Double) -> Unit,
    onSetTotalPlanned: (Double) -> Unit,
    onClearTotalPlanned: () -> Unit,
    onUpdateCategoryTitle: (String) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit
) {

    var groupMode by remember { mutableStateOf(GroupMode.Date) }

    // ── Date-wise grouping (your original) ──────────────────────────────
    val groupedByDate = remember(expenses) {
        expenses
            .sortedByDescending { it.updatedAt }
            .groupBy { formatToDateOnly(it.updatedAt) }
    }

    // ── Title-wise grouping (new for Option C) ─────────────────────────
    val groupedByTitle = remember(expenses) {
        expenses
            .groupBy { it.title }
            .map { (title, list) ->
                TitleGroup(
                    title = title,
                    total = list.sumOf { it.amount },
                    count = list.size,
                    transactions = list.sortedByDescending { it.updatedAt }
                )
            }
            .sortedByDescending { it.total }
    }

    var sheetState by remember { mutableStateOf<AddSheetState>(AddSheetState.Hidden) }
    val totalSpend = remember(expenses) { expenses.sumOf { it.amount } }
    val todayDate = formatToDateOnly(System.currentTimeMillis())
    val todaySpend = remember(groupedByDate) {
        groupedByDate[todayDate]?.sumOf { it.amount } ?: 0.0
    }
    var transactionTitle by remember { mutableStateOf("") }
    var transactionAmount by remember { mutableStateOf("") }
    var showDeleteSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showTotalExpenseSheet by remember { mutableStateOf(false) }
    var editingTitle by remember { mutableStateOf(selectedGroup.title) }
    // For expandable items (works in both modes)
    var expandedTransactions by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }
    val hasTotalPlanned = selectedGroup.totalPlanned > 0.0
    val remainingExpense = selectedGroup.totalPlanned - totalSpend

    fun closeSheet() {
        sheetState = AddSheetState.Hidden
        editingTitle = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = selectedGroup.title.toHeadlineCase(),
                modifier =  Modifier.padding(start = 12.dp),
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            //Edit Button
            IconButton(onClick = {
                editingTitle = selectedGroup.title
                sheetState = AddSheetState.EditingCategory(selectedGroup)
            }) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit Category",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Delete bin
            IconButton(onClick = { showDeleteSheet = true }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Category",
                    tint = Color.White.copy(alpha = 0.85f)
                )
            }

            // 3-dot menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "More Options",
                        tint = Color.White
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (hasTotalPlanned) "Remove Total Expense" else "Add Total Expense",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.LibraryAdd,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        onClick = {
                            showMenu = false
                            if (hasTotalPlanned) {
                                onClearTotalPlanned()
                            } else {
                                showTotalExpenseSheet = true
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "View Mode",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.GridView,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        onClick = {
                            showMenu = false
                            sheetState = AddSheetState.Choosing
                        }
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 20.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Spent",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        "₹${totalSpend.toInt()}",
                        color = Color.White,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black
                    )
                }

                if (todaySpend > 0) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.Top)
                    ) {
                        Text(
                            text = "Today: ₹${todaySpend.toInt()}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (hasTotalPlanned) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HeaderStat(
                        modifier = Modifier.weight(1f),
                        label = "Limit",
                        value = "₹${selectedGroup.totalPlanned.toInt()}"
                    )
                    HeaderStat(
                        modifier = Modifier.weight(1f),
                        label = if (remainingExpense.toInt() >= 0) "Remaining" else "Over-Limit",
                        value = remainingExpense.toInt().let{ if (it >= 0)  "₹${it}" else "₹${it*-1}"}
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = transactionAmount,
                        onValueChange = { transactionAmount = it },
                        placeholder = { Text("0") },
                        modifier = Modifier.width(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = transactionTitle,
                        onValueChange = { transactionTitle = it },
                        placeholder = {
                            Text(
                                "Expense description...",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (transactionTitle.isNotBlank() && transactionAmount != "0.0") {
                                onAddTransaction(transactionTitle, transactionAmount.toDoubleOrNull() ?: 0.0)
                                transactionTitle = ""
                                transactionAmount = "0.0"
                            }
                        },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCard,
                            contentDescription = "Add Expense",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.padding(top = 4.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .navigationBarsPadding(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                when (groupMode) {
                    GroupMode.Date -> {
                        groupedByDate.forEach { (date, transactionsForDate) ->
                            stickyHeader {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFFF7F7F7)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        ) {
                                            Text(
                                                text = if (date == todayDate) "Today" else date,
                                                modifier = Modifier.padding(
                                                    horizontal = 16.dp,
                                                    vertical = 4.dp
                                                ),
                                                style = MaterialTheme.typography.labelLarge,
                                                color = Color.DarkGray,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            items(transactionsForDate) { transaction ->
                                TransactionItem(
                                    transaction = transaction,
                                    onEdit = { /* TODO: Edit functionality in next step */ },
                                    onDelete = { transactionToDelete = it }
                                )
                            }
                        }
                    }
                    GroupMode.Title -> {
                        // New Title-wise grouping
                        items(groupedByTitle) { group ->
                            TitleGroupItem(
                                group = group,
                                isExpanded = expandedTransactions.contains(group.transactions.firstOrNull()?.id ?: 0),
                                onToggleExpand = {
                                    val firstId = group.transactions.firstOrNull()?.id ?: 0L
                                    expandedTransactions = if (expandedTransactions.contains(firstId)) {
                                        expandedTransactions - firstId
                                    } else {
                                        expandedTransactions + firstId
                                    }
                                },
                                onEdit = { /* TODO: connect later */ },
                                onDelete = { /* TODO: connect later */ }
                            )
                        }
                    }
                }
            }

        }
    }
    // ── Delete confirmation sheet ────────────────────────────────────
    if (showDeleteSheet) {
        DeleteConfirmationSheet(
            onDismiss = { showDeleteSheet = false },
            onConfirm = {
                showDeleteSheet = false
                onDeleteCategory()
            },
            title = "Category",
            groupName = selectedGroup.title
        )
    }
    if (transactionToDelete != null) {
        DeleteConfirmationSheet(
            onDismiss = { transactionToDelete = null },
            onConfirm = {
                transactionToDelete?.let { onDeleteTransaction(it) }
                transactionToDelete = null
            },
            title = "Transaction",
            groupName = transactionToDelete!!.title
        )
    }
    if (showTotalExpenseSheet) {
        TotalExpenseSheet(
            totalSpend = totalSpend,
            onDismiss = { showTotalExpenseSheet = false },
            onConfirm = { amount ->
                onSetTotalPlanned(amount)
                showTotalExpenseSheet = false
            }
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
                        title = "Choose your desired View Mode",
                        subtitle = "Sort by date or Group by Title",
                        firstChoiceTitle = "Date",
                        firstChoiceSubtitle = "Order of Date",
                        secondChoiceTitle = "Title",
                        secondChoiceSubtitle = "Group by Title",
                        firstIcon = Icons.Rounded.CalendarMonth,
                        secondIcon = Icons.Rounded.ViewStream,
                        selectedChoice = when (groupMode) {
                            GroupMode.Date -> 1
                            GroupMode.Title -> 2
                        },
                        onChooseFirst = {
                            groupMode = GroupMode.Date
                            closeSheet()
                        },
                        onChooseSecond = {
                            groupMode = GroupMode.Title
                            closeSheet()
                        }
                    )
                    is AddSheetState.EditingCategory -> EditCategorySheet(
                        currentTitle = selectedGroup.title,
                        value = editingTitle,
                        onValueChange = { editingTitle = it },
                        onConfirm = {
                            if (editingTitle.isNotBlank()) {
                                onUpdateCategoryTitle(editingTitle)
                                closeSheet()
                            }
                        },
                        onDismiss = {}
                    )

                    else -> {}
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {
    val expenses = remember {
        mutableStateListOf(
            Transaction(
                id = 0,
                title = "Petrol",
                amount = 1250.0,
                updatedAt = System.currentTimeMillis(),
                categoryId = 0
            ),
            Transaction(
                id = 1,
                title = "Dinner",
                amount = 1000.0,
                updatedAt = System.currentTimeMillis(),
                categoryId = 0
            ),
            Transaction(
                id = 2,
                title = "Toll",
                amount = 125.0,
                updatedAt = System.currentTimeMillis(),
                categoryId = 0
            ),
            Transaction(
                id = 3,
                title = "Hotel",
                amount = 2500.0,
                updatedAt = System.currentTimeMillis(),
                categoryId = 0
            )
        )
    }

    ListScreenContent(
        expenses = expenses,
        selectedGroup = Category(0, 0,"Grocery", System.currentTimeMillis()),
        onBackClick = {},
        onDeleteCategory = {},
        onAddTransaction = {} as (String, Double) -> Unit,
        onSetTotalPlanned = {},
        onClearTotalPlanned = {},
        onUpdateCategoryTitle = {},
        onDeleteTransaction = {}
    )
}

@Composable
fun TitleGroupItem(
    group: TitleGroup,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),           // softer, modern corners
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // ── HEADER with smooth amount pill transition ─────────────────
            AnimatedContent(
                targetState = isExpanded,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith
                            fadeOut(animationSpec = tween(180))
                },
                label = "AmountPillTransition"
            ) { expanded ->
                if (!expanded) {
                    // COLLAPSED: Title + amount pill BELOW it (exactly as in your image)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleExpand() }
                            .padding(horizontal = 18.dp, vertical = 16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = group.title.toHeadlineCase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1C1E)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        // Amount pill (below title in collapsed state)
                        Surface(
                            shape = RoundedCornerShape(30.dp),
                            color = Color(0xFF2C3E50),           // dark gray like your image
                            tonalElevation = 2.dp
                        ) {
                            Text(
                                text = "₹${group.total.toInt()}",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    // EXPANDED: Title + amount pill ON THE RIGHT (smooth move)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleExpand() }
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = group.title.toHeadlineCase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1C1E),
                            modifier = Modifier.weight(1f)
                        )

                        // Amount pill now on the right
                        Surface(
                            shape = RoundedCornerShape(30.dp),
                            color = Color(0xFF2C3E50),
                            tonalElevation = 2.dp
                        ) {
                            Text(
                                text = "₹${group.total.toInt()}",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowUp,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // ── Count text (only visible when expanded) ───────────────────
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(200)) + expandVertically(tween(300)),
                exit = fadeOut(tween(150))
            ) {
                Text(
                    text = "${group.count} Transactions",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(start = 18.dp, bottom = 4.dp)
                )
            }

            // ── Expanded List with left vertical bar (exact as in image) ──
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(320, easing = FastOutSlowInEasing)) + fadeIn(tween(220)),
                exit = shrinkVertically(animationSpec = tween(280)) + fadeOut(tween(180))
            ) {
                Column {
                    // Vertical black bar + list items
                    Row {
                        // Left vertical bar (exactly like your image)
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .padding(start = 18.dp, top = 4.dp, bottom = 12.dp)
                                .background(
                                    color = Color.Black,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )

                        // Transaction list
                        Column(modifier = Modifier.weight(1f)) {
                            group.transactions.forEach { transaction ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Amount
                                    Text(
                                        text = "₹${transaction.amount.toInt()}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1A1C1E)
                                    )

                                    Spacer(modifier = Modifier.weight(1f))

                                    // Time
                                    Text(
                                        text = formatToDateAndTime(transaction.updatedAt),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}