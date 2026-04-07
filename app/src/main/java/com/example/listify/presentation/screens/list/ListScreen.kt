package com.example.listify.presentation.screens.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Edit
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
import com.example.listify.domain.utils.formatToDateOnly
import com.example.listify.domain.utils.formatToTimeOnly
import com.example.listify.presentation.screens.composables.AddClusterSheet
import com.example.listify.presentation.screens.composables.DeleteConfirmationSheet
import com.example.listify.presentation.screens.composables.EditCategorySheet
import com.example.listify.presentation.screens.composables.HeaderStat
import com.example.listify.presentation.screens.composables.SingleFieldSheet
import com.example.listify.presentation.screens.composables.TotalExpenseSheet
import com.example.listify.presentation.screens.home.ChoosingSheet
import com.example.listify.presentation.screens.utils.AddSheetState

@Composable
fun ListScreen(
    listViewModel: ListViewModel = hiltViewModel(),
    onClick: () -> Unit
) {
    val expenses by listViewModel.transactions.collectAsState()
    val selectedGroup by listViewModel.selectedCategory.collectAsState()
    val groupedExpenses = remember(expenses) {
        expenses
            .sortedByDescending { it.updatedAt }
            .groupBy { formatToDateOnly(it.updatedAt) }
    }
    ListScreenContent(
        expenses = expenses,
        groupedExpenses = groupedExpenses,
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
    isExpanded: Boolean,
    onToggleExpand: (Long) -> Unit,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit
) {
    Card(
        onClick = { onToggleExpand(transaction.id) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(end = 48.dp)
                ) {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "₹${transaction.amount.toInt()}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = TextStyle(fontSize = 20.sp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = formatToTimeOnly(transaction.updatedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 200)),
                exit = shrinkVertically(
                    animationSpec = tween(durationMillis = 250)
                ) + fadeOut(animationSpec = tween(durationMillis = 200))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color(0xFFF1F3F4),
                        shape = RoundedCornerShape(28.dp),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            IconButton(
                                onClick = {  },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AddCircle,
                                    contentDescription = "Edit",
                                    tint = Color(0xFF1A1C1E),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            IconButton(
                                onClick = { onEdit(transaction) },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = "Edit",
                                    tint = Color(0xFF1A1C1E),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            IconButton(
                                onClick = { onDelete(transaction) },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListScreenContent(
    expenses: List<Transaction>,
    groupedExpenses: Map<String, List<Transaction>>,
    selectedGroup: Category,
    onBackClick: () -> Unit,
    onDeleteCategory: () -> Unit,
    onAddTransaction: (String, Double) -> Unit,
    onSetTotalPlanned: (Double) -> Unit,
    onClearTotalPlanned: () -> Unit,
    onUpdateCategoryTitle: (String) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit
) {

    var sheetState by remember { mutableStateOf<AddSheetState>(AddSheetState.Hidden) }
    val totalSpend = remember(expenses) { expenses.sumOf { it.amount } }
    val todayDate = formatToDateOnly(System.currentTimeMillis())
    val todaySpend = remember(groupedExpenses) {
        groupedExpenses[todayDate]?.sumOf { it.amount } ?: 0.0
    }
    var transactionTitle by remember { mutableStateOf("") }
    var transactionAmount by remember { mutableStateOf("") }
    var showDeleteSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showTotalExpenseSheet by remember { mutableStateOf(false) }
    var editingTitle by remember { mutableStateOf(selectedGroup.title) }
    var expandedTransactionId by remember { mutableStateOf<Long?>(null) }
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
                text = selectedGroup.title,
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
                groupedExpenses.forEach { (date, transactionsForDate) ->
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
                            isExpanded = expandedTransactionId == transaction.id,           // ← changed
                            onToggleExpand = { id ->
                                expandedTransactionId = if (expandedTransactionId == id) {
                                    null          // click again → collapse
                                } else {
                                    id            // click different item → expand this one and collapse others
                                }
                            },
                            onEdit = { /* TODO: Edit functionality in next step */ },
                            onDelete = { transactionToDelete = it }
                        )
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

    val groupedExpenses = remember {
        mutableStateMapOf(
            Pair(formatToDateOnly(System.currentTimeMillis()), expenses)
        )
    }
    ListScreenContent(
        expenses = expenses,
        groupedExpenses = groupedExpenses,
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


