package com.example.listify.presentation.screens.receiptscanner


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.listify.domain.model.HomeScreenData
import com.example.listify.domain.model.ReceiptItem
import com.example.listify.domain.model.ReceiptScanResult
import com.example.listify.domain.model.ScannedReceipt
import com.example.listify.presentation.screens.composables.sheets.SelectCategorySheet
import java.io.File

private val HeaderGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1B5548), Color(0xFF266B5C))
)

@Composable
fun ReceiptScanScreen(
    viewModel: ReceiptScanViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val scanResult by viewModel.scanResult.collectAsState()
    val selectedItems by viewModel.selectedItems.collectAsState()
    val homeScreenData by viewModel.homeScreenData.collectAsState()
    val addSuccess by viewModel.addSuccess.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(addSuccess) {

        if (addSuccess) {
            viewModel.resetScan()
            onBackClick()
        }
    }

    ReceiptScanContent(
        scanResult = scanResult,
        selectedItems = selectedItems,
        homeScreenData = homeScreenData,
        context = context,
        onImageSelected = { bitmap -> viewModel.scanReceipt(bitmap) },
        onToggleItem = viewModel::toggleItemSelection,
        onAddToCategory = { categoryId, receipt ->
            viewModel.addSelectedItemsToCategory(categoryId, receipt)
        },
        onReset = viewModel::resetScan,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScanContent(
    scanResult: ReceiptScanResult,
    selectedItems: Set<Int>,
    homeScreenData: HomeScreenData,
    context: Context,
    onImageSelected: (Bitmap) -> Unit,
    onToggleItem: (Int) -> Unit,
    onAddToCategory: (Long, ScannedReceipt) -> Unit,
    onReset: () -> Unit,
    onBackClick: () -> Unit
) {
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showCategorySheet by remember { mutableStateOf(false) }

    val imageFile = remember {
        File(context.cacheDir, "images").apply { mkdirs() }
            .let { File(it, "receipt_${System.currentTimeMillis()}.jpg") }
    }
    val imageUri = remember(imageFile) {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val bmp = BitmapFactory.decodeFile(imageFile.absolutePath)
            capturedBitmap = bmp
            capturedImageUri = imageUri
            bmp?.let { onImageSelected(it) }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            capturedImageUri = it
            val stream = context.contentResolver.openInputStream(it)
            val bmp = BitmapFactory.decodeStream(stream)
            capturedBitmap = bmp
            bmp?.let { bitmap -> onImageSelected(bitmap) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2EDE4))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = HeaderGradient)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Scan Receipt",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Powered by ML Kit + Gemini Nano",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.65f)
                )
            }
            if (scanResult !is ReceiptScanResult.Idle) {
                IconButton(
                    onClick = onReset,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.RestartAlt,
                        contentDescription = "Scan again",
                        tint = Color.White
                    )
                }
            }
        }

        AnimatedContent(
            targetState = scanResult,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
            label = "ScanState"
        ) { state ->
            when (state) {
                is ReceiptScanResult.Idle -> IdleState(
                    onCameraClick = { cameraLauncher.launch(imageUri) },
                    onGalleryClick = { galleryLauncher.launch("image/*") }
                )

                is ReceiptScanResult.Scanning -> ScanningState(
                    capturedImageUri = capturedImageUri
                )

                is ReceiptScanResult.Success -> SuccessState(
                    receipt = state.receipt,
                    selectedItems = selectedItems,
                    capturedImageUri = capturedImageUri,
                    onToggleItem = onToggleItem,
                    onAddToListify = { showCategorySheet = true }
                )

                is ReceiptScanResult.Error -> ErrorState(
                    message = state.message,
                    onRetry = {
                        onReset()
                        capturedBitmap?.let { onImageSelected(it) }
                    },
                    onRescan = {
                        onReset()
                        galleryLauncher.launch("image/*")
                    }
                )
            }
        }
    }

    if (showCategorySheet && scanResult is ReceiptScanResult.Success) {
        val receipt = (scanResult as ReceiptScanResult.Success).receipt
        ModalBottomSheet(
            onDismissRequest = { showCategorySheet = false },
            containerColor = Color.White,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            CategoryPickerSheet(
                homeScreenData = homeScreenData,
                onCategorySelected = { categoryId ->
                    onAddToCategory(categoryId, receipt)
                    showCategorySheet = false
                },
                onDismiss = { showCategorySheet = false }
            )
        }
    }
}

@Composable
private fun IdleState(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.DocumentScanner,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(52.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Scan a Receipt",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1C1E)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "ML Kit reads the text, Gemini Nano\nextract items — all on your device.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = onCameraClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Rounded.Camera, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text("Take Photo", fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onGalleryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                Icons.Rounded.Image, null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Choose from Gallery",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF43A047).copy(alpha = 0.08f))
                .padding(12.dp)
        ) {
            Icon(
                Icons.Rounded.Info, null,
                tint = Color(0xFF43A047),
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Your receipt never leaves this device. Processed 100% locally via Gemini Nano.",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF43A047)
            )
        }
    }
}

@Composable
private fun ScanningState(capturedImageUri: Uri?) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (capturedImageUri != null) {
            AsyncImage(
                model = capturedImageUri,
                contentDescription = "Scanned receipt",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .graphicsLayer { alpha = 0.65f }
            )
            Spacer(Modifier.height(24.dp))
        }

        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(52.dp)
                .graphicsLayer { scaleX = pulse; scaleY = pulse }
        )
        Spacer(Modifier.height(20.dp))

        Text(
            text = "Analysing Receipt…",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1C1E)
        )
        Spacer(Modifier.height(8.dp))

        ScanStep(step = 1, label = "ML Kit — extracting text", done = true)
        ScanStep(step = 2, label = "Gemini Nano — parsing items", done = false, active = true)
        ScanStep(step = 3, label = "Building structured output", done = false)
    }
}

@Composable
private fun ScanStep(step: Int, label: String, done: Boolean, active: Boolean = false) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = when {
                done -> MaterialTheme.colorScheme.primary
                active -> MaterialTheme.colorScheme.primary.copy(alpha = 0.30f)
                else -> Color(0xFFE5E7EB)
            },
            shape = CircleShape,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (done) {
                    Icon(Icons.Rounded.Check, null,
                        tint = Color.White, modifier = Modifier.size(14.dp))
                } else {
                    Text(
                        text = step.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (active) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = when {
                done -> Color(0xFF1A1C1E)
                active -> MaterialTheme.colorScheme.primary
                else -> Color(0xFF9CA3AF)
            },
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}


@Composable
private fun SuccessState(
    receipt: ScannedReceipt,
    selectedItems: Set<Int>,
    capturedImageUri: Uri?,
    onToggleItem: (Int) -> Unit,
    onAddToListify: () -> Unit
) {
    val selectedTotal = remember(selectedItems, receipt) {
        receipt.items.filterIndexed { i, _ -> i in selectedItems }.sumOf { it.price }
            .let { if (it == 0.0) receipt.totalAmount else it }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        capturedImageUri?.let { uri ->
            item(key = "image") {
                AsyncImage(
                    model = uri,
                    contentDescription = "Receipt",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(18.dp))
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        item(key = "merchant") {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Receipt, null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp))
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = receipt.merchantName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E)
                        )
                        if (receipt.date.isNotBlank()) {
                            Text(
                                text = receipt.date,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "₹${receipt.totalAmount.toInt()}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Items header
        if (receipt.items.isNotEmpty()) {
            item(key = "items_header") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Items (${receipt.items.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E)
                    )
                    Text(
                        text = "Select to add",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9CA3AF)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            itemsIndexed(receipt.items, key = { i, _ -> "item_$i" }) { index, item ->
                ReceiptItemRow(
                    item = item,
                    isSelected = index in selectedItems,
                    onToggle = { onToggleItem(index) }
                )
                Spacer(Modifier.height(6.dp))
            }
        }

        item(key = "footer") {
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFE5E7EB))
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Selected Total",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = "₹${"%.2f".format(selectedTotal)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onAddToListify,
                modifier = Modifier.fillMaxWidth().height(56.dp)
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Rounded.CheckCircle, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Add to Listify",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    onRescan: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = Color(0xFFE53935).copy(alpha = 0.10f),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.Warning, null,
                    tint = Color(0xFFE53935), modifier = Modifier.size(40.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("Scan Failed", style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
        Spacer(Modifier.height(8.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onRetry, modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
            Text("Retry with Same Image", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onRescan, modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)) {
            Text("Choose Different Image", fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalLayoutApi::class)
@Composable
private fun CategoryPickerSheet(
    homeScreenData: HomeScreenData,
    onCategorySelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = 48.dp,
            start = 24.dp,
            end = 24.dp
        )
    ) {
        item(key = "title") {
            Text("Add receipt items to…",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1C1E))
            Spacer(Modifier.height(16.dp))
        }
        itemsIndexed(homeScreenData.clusteredSections,
            key = { _, s -> "cluster_${s.cluster.id}" }) { _, section ->
            Text(text = section.cluster.name.uppercase(),
                style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = TextUnit(1.4f,
                    TextUnitType.Sp)
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                section.categories.forEach { category ->
                    FilterChip(
                        selected = false,
                        onClick = { onCategorySelected(category.id) },
                        label = { Text(category.title) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        if (homeScreenData.generalCategories.isNotEmpty()) {
            item(key = "general") {
                Text("GENERAL", style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary,
                    letterSpacing = androidx.compose.ui.unit.TextUnit(1.4f,
                        TextUnitType.Sp))
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    homeScreenData.generalCategories.forEach { category ->
                        FilterChip(
                            selected = false,
                            onClick = { onCategorySelected(category.id) },
                            label = { Text(category.title) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }
    }
}