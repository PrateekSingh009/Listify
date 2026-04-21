package com.example.listify.presentation.screens.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.CalendarMonth
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.listify.domain.model.Category
import com.example.listify.domain.model.Transaction
import com.example.listify.domain.model.TransactionTitleGroup
import com.example.listify.domain.utils.formatToDateOnly
import com.example.listify.presentation.screens.composables.sheets.ChoosingSheet
import com.example.listify.presentation.screens.composables.sheets.DeleteConfirmationSheet
import com.example.listify.presentation.screens.composables.sheets.EditCategorySheet
import com.example.listify.presentation.screens.composables.HeaderStat
import com.example.listify.presentation.screens.composables.sheets.TotalExpenseSheet
import com.example.listify.presentation.screens.composables.sheets.TransactionFormSheet
import com.example.listify.presentation.screens.utils.AddSheetState
import com.example.listify.presentation.screens.utils.GroupMode
import com.example.listify.presentation.screens.utils.extensions.toHeadlineCase

@Composable
fun ListScreen(
    listViewModel: ListViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val expenses by listViewModel.transactions.collectAsState()
    val selectedGroup by listViewModel.selectedCategory.collectAsState()
    ListScreenContent(
        expenses = expenses,
        selectedGroup = selectedGroup,
        onBackClick = onBackClick,
        onDeleteCategory =  {
            onBackClick()
            listViewModel.deleteCategory(selectedGroup)
        },
        onAddTransaction = listViewModel::addTransaction,
        onSetTotalPlanned = listViewModel::setTotalPlanned,
        onClearTotalPlanned = listViewModel::clearTotalPlanned,
        onUpdateCategoryTitle = listViewModel::updateCategoryTitle,
        onDeleteTransaction = listViewModel::deleteTransaction,
        onUpdateTransaction = listViewModel::updateTransaction
    )
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
    onDeleteTransaction: (Transaction) -> Unit,
    onUpdateTransaction: (Transaction, String, Double) -> Unit
) {

    var groupMode by remember { mutableStateOf(GroupMode.Date) }

    val groupedByDate = remember(expenses) {
        expenses
            .sortedByDescending { it.updatedAt }
            .groupBy { formatToDateOnly(it.updatedAt) }
    }

    val groupedByTitle = remember(expenses) {
        expenses
            .groupBy { it.title }
            .map { (title, list) ->
                TransactionTitleGroup(
                    title = title,
                    total = list.sumOf { it.amount },
                    count = list.size,
                    transactions = list.sortedByDescending { it.updatedAt }
                )
            }
            .sortedByDescending { it.total }
    }

    val uniqueTitles = remember(expenses) {
        expenses.map { it.title }.distinct().sorted()
    }

    var sheetState by remember { mutableStateOf<AddSheetState>(AddSheetState.Hidden) }

    val totalSpend = remember(expenses) { expenses.sumOf { it.amount } }
    val todayDate = formatToDateOnly(System.currentTimeMillis())
    val todaySpend = remember(groupedByDate) { groupedByDate[todayDate]?.sumOf { it.amount } ?: 0.0 }

    var transactionTitle by remember { mutableStateOf("") }
    var transactionAmount by remember { mutableStateOf("") }

    var showDeleteSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showTotalExpenseSheet by remember { mutableStateOf(false) }

    var editingTitle by remember { mutableStateOf(selectedGroup.title) }

    var expandedTransactions by remember { mutableStateOf<Set<Long>>(emptySet()) }

    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    val hasTotalPlanned = selectedGroup.totalPlanned > 0.0

    val remainingExpense = selectedGroup.totalPlanned - totalSpend

    var showAddTransactionSheet by remember { mutableStateOf(false) }

    var showEditTransactionSheet by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }

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

            IconButton(onClick = { showDeleteSheet = true }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Category",
                    tint = Color.White.copy(alpha = 0.85f)
                )
            }

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
                Button(
                    onClick = { showAddTransactionSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3857))
                ) {
                    Icon(
                        Icons.Filled.AddCard,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Add Expense", fontWeight = FontWeight.Bold)
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
                                TransactionItemByDate(
                                    transaction = transaction,
                                    onEdit = {
                                        editingTransaction = it
                                        showEditTransactionSheet = true
                                    },
                                    onDelete = { transactionToDelete = it }
                                )
                            }
                        }
                    }
                    GroupMode.Title -> {
                        items(groupedByTitle) { group ->
                            TransactionItemByTitle(
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

    if (showAddTransactionSheet || (showEditTransactionSheet && editingTransaction != null)) {
        TransactionFormSheet(
            isEditMode = showEditTransactionSheet,
            initialTransaction = editingTransaction,
            uniqueTitles = uniqueTitles,
            onSave = { title, amount ->
                if (showEditTransactionSheet && editingTransaction != null) {
                    onUpdateTransaction(editingTransaction!!, title, amount)
                } else {
                    onAddTransaction(title, amount)
                }
            },
            onDismiss = {
                showAddTransactionSheet = false
                showEditTransactionSheet = false
                editingTransaction = null
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
        onDeleteTransaction = {},
        onUpdateTransaction = {} as (Transaction,String, Double) -> Unit,
    )
}