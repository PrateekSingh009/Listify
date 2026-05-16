package com.example.listify.presentation.screens.receiptscanner

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listify.domain.model.ReceiptItem
import com.example.listify.domain.model.ReceiptScanResult
import com.example.listify.domain.model.ScannedReceipt
import com.example.listify.domain.usecase.AddTransactionToCategoryUseCase
import com.example.listify.domain.usecase.GetHomeScreenDataUseCase
import com.example.listify.domain.model.HomeScreenData
import com.example.listify.domain.usecase.ScannedReceiptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiptScanViewModel @Inject constructor(
    private val scannedReceiptUseCase: ScannedReceiptUseCase,
    private val addTransactionToCategoryUseCase: AddTransactionToCategoryUseCase,
    getHomeScreenDataUseCase: GetHomeScreenDataUseCase
) : ViewModel() {

    val homeScreenData: StateFlow<HomeScreenData> = getHomeScreenDataUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L),
            HomeScreenData(emptyList(), emptyList()))

    private val _scanResult = MutableStateFlow<ReceiptScanResult>(ReceiptScanResult.Idle)
    val scanResult: StateFlow<ReceiptScanResult> = _scanResult.asStateFlow()
    private val _selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    val selectedItems: StateFlow<Set<Int>> = _selectedItems.asStateFlow()

    private val _addSuccess = MutableStateFlow(false)
    val addSuccess: StateFlow<Boolean> = _addSuccess.asStateFlow()

    fun scanReceipt(bitmap: Bitmap) {
        viewModelScope.launch {
            _scanResult.value = ReceiptScanResult.Scanning
            scannedReceiptUseCase(bitmap)
                .onSuccess { receipt ->
                    _scanResult.value = ReceiptScanResult.Success(receipt)
                    _selectedItems.value = receipt.items.indices.toSet()
                }
                .onFailure { error ->
                    _scanResult.value = ReceiptScanResult.Error(
                        error.message ?: "Could not scan receipt. Please try again."
                    )
                }
        }
    }

    fun toggleItemSelection(index: Int) {
        val current = _selectedItems.value.toMutableSet()
        if (current.contains(index)) current.remove(index) else current.add(index)
        _selectedItems.value = current
    }

    fun addSelectedItemsToCategory(categoryId: Long, receipt: ScannedReceipt) {
        viewModelScope.launch {
            val selected = _selectedItems.value
            val itemsToAdd = receipt.items.filterIndexed { index, _ -> index in selected }

            val transactions = if (itemsToAdd.isEmpty()) {
                listOf(receipt.merchantName to receipt.totalAmount)
            } else {
                itemsToAdd.map { it.name to it.price }
            }

            transactions.forEach { (name, amount) ->
                if (amount > 0) {
                    addTransactionToCategoryUseCase(name, amount, categoryId)
                }
            }

            _addSuccess.value = true
        }
    }

    fun resetScan() {
        _scanResult.value = ReceiptScanResult.Idle
        _selectedItems.value = emptySet()
        _addSuccess.value = false
    }
}