package com.example.listify.domain.usecase

import com.example.listify.domain.model.DetectedPayment
import com.example.listify.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLatestActivePaymentUseCase @Inject constructor(
    private val repository: DataRepository
) {
    operator fun invoke(): Flow<DetectedPayment?> =
        repository.getLatestUnprocessedPayment()
}