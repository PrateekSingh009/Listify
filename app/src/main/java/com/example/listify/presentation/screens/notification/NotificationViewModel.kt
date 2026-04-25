package com.example.listify.presentation.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listify.domain.model.DetectedPayment
import com.example.listify.domain.model.HomeScreenData
import com.example.listify.domain.usecase.AddTransactionToCategoryUseCase
import com.example.listify.domain.usecase.GetHomeScreenDataUseCase
import com.example.listify.domain.usecase.GetProcessedDetectedPaymentsUseCase
import com.example.listify.domain.usecase.GetUnprocessedDetectedPaymentsUseCase
import com.example.listify.domain.usecase.MarkDetectedPaymentAsProcessedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getUnprocessedDetectedPaymentsUseCase: GetUnprocessedDetectedPaymentsUseCase,
    private val getProcessedDetectedPaymentsUseCase: GetProcessedDetectedPaymentsUseCase,
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
    private val addTransactionToCategoryUseCase: AddTransactionToCategoryUseCase,
    private val markDetectedPaymentAsProcessedUseCase: MarkDetectedPaymentAsProcessedUseCase
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val unprocessedPayments: StateFlow<List<DetectedPayment>> =
        getUnprocessedDetectedPaymentsUseCase()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    val processedPayments: StateFlow<List<DetectedPayment>> =
        getProcessedDetectedPaymentsUseCase()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    // Needed to populate SelectCategorySheet
    val homeScreenData: StateFlow<HomeScreenData> = getHomeScreenDataUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            HomeScreenData(emptyList(), emptyList())
        )

    fun addPaymentToCategory(payment: DetectedPayment, categoryId: Long, title: String, amount: Double) {
        viewModelScope.launch {
            addTransactionToCategoryUseCase(title, amount, categoryId)
                .onSuccess {
                    markDetectedPaymentAsProcessedUseCase(payment.id)
                    _error.value = null
                }
                .onFailure { _error.value = "Could not add transaction" }
        }
    }
}