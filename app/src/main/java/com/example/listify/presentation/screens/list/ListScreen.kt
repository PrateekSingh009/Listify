package com.example.listify.presentation.screens.list

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.net.Uri
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
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.listify.domain.model.Category
import com.example.listify.domain.model.Transaction
import com.example.listify.domain.model.TransactionTitleGroup
import com.example.listify.domain.utils.formatToDateOnly
import com.example.listify.presentation.screens.composables.HeaderStat
import com.example.listify.presentation.screens.composables.sheets.ChoosingSheet
import com.example.listify.presentation.screens.composables.sheets.DeleteConfirmationSheet
import com.example.listify.presentation.screens.composables.sheets.EditCategorySheet
import com.example.listify.presentation.screens.composables.sheets.TotalExpenseSheet
import com.example.listify.presentation.screens.composables.sheets.TransactionFormSheet
import com.example.listify.presentation.screens.utils.AddSheetState
import com.example.listify.presentation.screens.utils.GroupMode
import com.example.listify.presentation.screens.utils.extensions.toHeadlineCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ListScreen(
    listViewModel: ListViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val expenses by listViewModel.transactions.collectAsState()
    val selectedGroup by listViewModel.selectedCategory.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
        onUpdateTransaction = listViewModel::updateTransaction,
        onShareClick = {
            scope.launch {
                generateAndSharePdf(context, selectedGroup.title, expenses)
            }
        }
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
    onUpdateTransaction: (Transaction, String, Double) -> Unit,
    onShareClick: () -> Unit
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
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
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
                        modifier =  Modifier.padding(start = 12.dp).weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

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
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Share Data",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Share,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onShareClick()
                                }
                            )
                        }
                    }
                }
            }
        }
        HeaderBar(
            title = selectedGroup.title,
            totalSpend = totalSpend,
            todaySpend = todaySpend,
            totalPlanned = selectedGroup.totalPlanned,
            onAddExpenseClick = { showAddTransactionSheet = true }
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF2EDE4))
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
        onShareClick = {}
    )
}

private suspend fun generateAndSharePdf(
    context: Context,
    categoryName: String,
    transactions: List<Transaction>
) {
    withContext(Dispatchers.IO) {
        try {
            val pdfDocument = PdfDocument()
            val paint = Paint()
            val titlePaint = Paint().apply {
                textSize = 24f
                isFakeBoldText = true
            }
            val headerPaint = Paint().apply {
                textSize = 14f
                isFakeBoldText = true
            }
            val bodyPaint = Paint().apply {
                textSize = 12f
            }

            val pageWidth = 595
            val pageHeight = 842

            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas

            var y = 50f
            val startX = 40f
            val endX = pageWidth - 40f
            val contentWidth = endX - startX

            titlePaint.textAlign = Paint.Align.CENTER
            canvas.drawText("Category: ${categoryName.toHeadlineCase()}", pageWidth / 2f, y, titlePaint)
            y += 20f
            titlePaint.textSize = 12f
            titlePaint.isFakeBoldText = false
            canvas.drawText("Generated by Listify on ${
                SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                ).format(Date())}", pageWidth / 2f, y, titlePaint as android.graphics.Paint
            )
            y += 30f

            headerPaint.textAlign = Paint.Align.LEFT
            canvas.drawText("Title", startX, y, headerPaint)
            canvas.drawText("Amount", startX + contentWidth * 0.60f, y, headerPaint)
            canvas.drawText("Date", startX + contentWidth * 0.80f, y, headerPaint)
            y += 5f
            canvas.drawLine(startX, y, endX, y, paint)
            y += 20f

            val totalAmount = transactions.sumOf { it.amount }
            val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

            for (transaction in transactions.sortedByDescending { it.updatedAt }) {
                if (y > pageHeight - 80) {
                    pdfDocument.finishPage(page)
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.pages.size + 1).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    y = 50f
                }

                bodyPaint.textAlign = Paint.Align.LEFT

                val title = if (transaction.title.length > 20) "${transaction.title.take(20)}..." else transaction.title
                canvas.drawText(title, startX, y, bodyPaint)

                canvas.drawText("₹%.2f".format(transaction.amount), startX + contentWidth * 0.60f, y, bodyPaint)
                canvas.drawText(dateFormat.format(Date(transaction.updatedAt)), startX + contentWidth * 0.80f, y, bodyPaint)

                y += 25f
            }

            y += 10f
            canvas.drawLine(startX, y, endX, y, paint)
            y += 20f
            headerPaint.textAlign = Paint.Align.LEFT
            canvas.drawText("Total Spent:", startX, y, headerPaint)
            bodyPaint.isFakeBoldText = true
            bodyPaint.textAlign = Paint.Align.LEFT
            canvas.drawText("₹%.2f".format(totalAmount), startX + contentWidth * 0.60f, y, bodyPaint)

            pdfDocument.finishPage(page)

            val fileName = "Listify_${categoryName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
            val cacheDir = context.cacheDir
            val file = File(cacheDir, fileName)
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            withContext(Dispatchers.Main) {
                context.startActivity(Intent.createChooser(shareIntent, "Share Transaction List"))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}