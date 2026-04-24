package com.example.listify.domain.usecase

import com.example.listify.domain.model.DetectedPayment
import com.example.listify.domain.repository.DataRepository
import javax.inject.Inject

class SaveDetectedPaymentUseCase @Inject constructor(
    private val repository: DataRepository
) {
    suspend operator fun invoke(payment: DetectedPayment) =
        runCatching { repository.saveDetectedPayment(payment) }
}