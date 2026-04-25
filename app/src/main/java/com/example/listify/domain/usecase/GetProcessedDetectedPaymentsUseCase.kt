package com.example.listify.domain.usecase

import com.example.listify.domain.model.DetectedPayment
import com.example.listify.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProcessedDetectedPaymentsUseCase @Inject constructor(
    private val repository: DataRepository
) {
    operator fun invoke(): Flow<List<DetectedPayment>> =
        repository.getProcessedDetectedPayments()
}