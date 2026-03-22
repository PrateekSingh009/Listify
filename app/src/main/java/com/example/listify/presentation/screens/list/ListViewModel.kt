package com.example.listify.presentation.screens.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listify.domain.model.Category
import com.example.listify.domain.model.Transaction
import com.example.listify.domain.usecase.AddTransactionUseCase
import com.example.listify.domain.usecase.DeleteTransactionUseCase
import com.example.listify.domain.usecase.GetCategoryByIdUseCase
import com.example.listify.domain.usecase.GetTransactionListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTransactionListUseCase: GetTransactionListUseCase,
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
): ViewModel() {
    private val groupId: Long = checkNotNull(savedStateHandle["groupId"])

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val transactions : StateFlow<List<Transaction>> = getTransactionListUseCase(groupId)
        .stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000L),emptyList())

    val selectedCategory : StateFlow<Category> = getCategoryByIdUseCase(groupId)
        .stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000L),Category(0,0,"",0))

    fun addTransaction(title: String, amount: Double) {
        viewModelScope.launch {
            addTransactionUseCase(title,amount,groupId)
                .onSuccess {
                    _error.value = null
                }
                .onFailure { _error.value = it.message }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            deleteTransactionUseCase(transaction).onFailure {
                _error.value = "Could not delete transaction"
            }
        }
    }
}