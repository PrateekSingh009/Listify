package com.example.listify.domain.usecase

import com.example.listify.domain.model.Transaction
import com.example.listify.domain.repository.DataRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: DataRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        return try {
            repository.deleteTransaction(transaction)
            repository.updateLastUpdated(transaction.categoryId, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}