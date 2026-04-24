package com.example.listify.domain.usecase

import com.example.listify.domain.repository.DataRepository
import javax.inject.Inject

class MarkDetectedPaymentAsProcessedUseCase @Inject constructor(
    private val repository: DataRepository
) {
    suspend operator fun invoke(id: Long) =
        runCatching { repository.markDetectedPaymentAsProcessed(id) }
}