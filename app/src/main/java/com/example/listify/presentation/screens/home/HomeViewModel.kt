package com.example.listify.presentation.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listify.domain.model.Category
import com.example.listify.domain.model.DetectedPayment
import com.example.listify.domain.model.HomeScreenData
import com.example.listify.domain.repository.DataRepository
import com.example.listify.domain.usecase.AddCategoryUseCase
import com.example.listify.domain.usecase.AddClusterUseCase
import com.example.listify.domain.usecase.AddTransactionToCategoryUseCase
import com.example.listify.domain.usecase.DeleteCategoryUseCase
import com.example.listify.domain.usecase.GetHomeScreenDataUseCase
import com.example.listify.domain.usecase.GetLatestActivePaymentUseCase
import com.example.listify.domain.usecase.GetUnprocessedDetectedPaymentsUseCase
import com.example.listify.domain.usecase.MarkDetectedPaymentAsProcessedUseCase
import com.example.listify.domain.usecase.SaveDetectedPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val addClusterUseCase: AddClusterUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val getLatestActivePaymentUseCase: GetLatestActivePaymentUseCase,
    private val getUnprocessedDetectedPaymentsUseCase: GetUnprocessedDetectedPaymentsUseCase,
    private val saveDetectedPaymentUseCase: SaveDetectedPaymentUseCase,
    private val markDetectedPaymentAsProcessedUseCase: MarkDetectedPaymentAsProcessedUseCase,
    private val addTransactionToCategoryUseCase: AddTransactionToCategoryUseCase
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val homeScreenData: StateFlow<HomeScreenData> = getHomeScreenDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeScreenData(emptyList(), emptyList())
        )

    val latestUnprocessedPayment: StateFlow<DetectedPayment?> = getLatestActivePaymentUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), null)


    private val _activePrompt = MutableStateFlow<DetectedPayment?>(null)
    val activePrompt: StateFlow<DetectedPayment?> = _activePrompt.asStateFlow()

    init {
        viewModelScope.launch {
            getLatestActivePaymentUseCase()
                .collect { payment ->
                    if (_activePrompt.value == null && payment != null) {
                        _activePrompt.value = payment
                    }
                }
        }
    }

    fun ignorePrompt() {
        _activePrompt.value = null
    }

    fun addCategory(title: String, clusterId: Long? = null) {
        viewModelScope.launch {
            addCategoryUseCase(title, clusterId)
                .onSuccess { _error.value = null }
                .onFailure { _error.value = it.message }
        }
    }

    fun addCluster(clusterName: String, firstCategoryName: String) {
        viewModelScope.launch {
            addClusterUseCase(clusterName, firstCategoryName)
                .onSuccess { _error.value = null }
                .onFailure { _error.value = it.message }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            deleteCategoryUseCase(category).onFailure {
                _error.value = "Could not delete category"
            }
        }
    }

    val unprocessedDetectedPayments: StateFlow<List<DetectedPayment>> =
        getUnprocessedDetectedPaymentsUseCase()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())


    fun addDetectedPaymentToCategory(paymentId: Long, categoryId: Long,finalTitle: String,finalAmount:Double) {
        viewModelScope.launch {
            addTransactionToCategoryUseCase(
                title = finalTitle,
                amount = finalAmount,
                categoryId = categoryId
            )
            markDetectedPaymentAsProcessed(paymentId)
            _activePrompt.value = null
        }
    }

    fun markDetectedPaymentAsProcessed(id: Long) {
        viewModelScope.launch {
            markDetectedPaymentAsProcessedUseCase(id)
            _activePrompt.value = null
        }
    }

    fun addDetectedPayment(payment: DetectedPayment) {
        viewModelScope.launch {
            saveDetectedPaymentUseCase(payment)
        }
    }
}