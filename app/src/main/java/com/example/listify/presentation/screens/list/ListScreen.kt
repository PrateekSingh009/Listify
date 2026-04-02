package com.example.listify.presentation.screens.list

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
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
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
import com.example.listify.presentation.screens.utils.DeleteConfirmationSheet
import com.example.listify.presentation.screens.utils.HeaderStat

private lateinit var viewModel: ListViewModel

@Composable
fun ListScreen(
    listViewModel: ListViewModel = hiltViewModel(),
    onClick: () -> Unit
) {
    viewModel = listViewModel
    val expenses by viewModel.transactions.collectAsState()
    val selectedGroup by viewModel.selectedCategory.collectAsState()
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
            viewModel.deleteCategory(selectedGroup)
            onClick()
        }
    )
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
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
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListScreenContent(
    expenses: List<Transaction>,
    groupedExpenses: Map<String, List<Transaction>>,
    selectedGroup: Category,
    onBackClick: () -> Unit,
    onDeleteCategory: () -> Unit,
) {

    val totalSpend = remember(expenses) { expenses.sumOf { it.amount } }
    val todayDate = formatToDateOnly(System.currentTimeMillis())
    val todaySpend = remember(groupedExpenses) {
        groupedExpenses[todayDate]?.sumOf { it.amount } ?: 0.0
    }
    var transactionTitle by remember { mutableStateOf("") }
    var transactionAmount by remember { mutableStateOf("") }
    var showDeleteSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

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
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Add Total Expense",
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
                        text = "Total Spend",
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HeaderStat(
                    modifier = Modifier.weight(1f),
                    label = "Total Expense",
                    value = 10000.toString()
                )
                HeaderStat(
                    modifier = Modifier.weight(1f),
                    label = "Remaining Expense",
                    value = (10000-4875).toString()
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

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
                                viewModel.addTransaction(transactionTitle, transactionAmount.toDoubleOrNull() ?: 0.0)
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
                        TransactionItem(transaction)
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
            groupName = selectedGroup.title
        )
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
        onDeleteCategory = {}
    )
}


