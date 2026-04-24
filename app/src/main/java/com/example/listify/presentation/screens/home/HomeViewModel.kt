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
import com.example.listify.domain.usecase.GetUnprocessedDetectedPaymentsUseCase
import com.example.listify.domain.usecase.MarkDetectedPaymentAsProcessedUseCase
import com.example.listify.domain.usecase.SaveDetectedPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val addClusterUseCase: AddClusterUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val getUnprocessedDetectedPaymentsUseCase: GetUnprocessedDetectedPaymentsUseCase,
    private val saveDetectedPaymentUseCase: SaveDetectedPaymentUseCase,
    private val markDetectedPaymentAsProcessedUseCase: MarkDetectedPaymentAsProcessedUseCase,
    private val addTransactionToCategoryUseCase: AddTransactionToCategoryUseCase
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _ignoredPaymentIds = MutableStateFlow<Set<Long>>(emptySet())

    val ignoredPaymentIds: StateFlow<Set<Long>> = _ignoredPaymentIds.asStateFlow()

    val homeScreenData: StateFlow<HomeScreenData> = getHomeScreenDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeScreenData(emptyList(), emptyList())
        )

    /** Adds a general category (clusterId = null) or one inside an existing cluster. */
    fun addCategory(title: String, clusterId: Long? = null) {
        viewModelScope.launch {
            addCategoryUseCase(title, clusterId)
                .onSuccess { _error.value = null }
                .onFailure { _error.value = it.message }
        }
    }

    /** Creates a new cluster + its first category in one atomic write. */
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


    fun ignoreDetectedPayment(paymentId: Long) {
        _ignoredPaymentIds.value += paymentId
    }

    fun addDetectedPaymentToCategory(paymentId: Long, categoryId: Long,finalTitle: String,finalAmount:Double) {
        viewModelScope.launch {
            addTransactionToCategoryUseCase(
                title = finalTitle,
                amount = finalAmount,
                categoryId = categoryId
            )
            markDetectedPaymentAsProcessed(paymentId)
        }
    }

    fun markDetectedPaymentAsProcessed(id: Long) {
        viewModelScope.launch {
            markDetectedPaymentAsProcessedUseCase(id)
            _ignoredPaymentIds.value -= id
        }
    }

    // Optional: If you want to add the detected payment directly
    fun addDetectedPayment(payment: DetectedPayment) {
        viewModelScope.launch {
            saveDetectedPaymentUseCase(payment)
        }
    }
}