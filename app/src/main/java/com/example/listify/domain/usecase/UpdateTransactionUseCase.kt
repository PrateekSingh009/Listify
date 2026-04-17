package com.example.listify.domain.usecase

import com.example.listify.domain.model.Category
import com.example.listify.domain.model.Transaction
import com.example.listify.domain.repository.DataRepository
import javax.inject.Inject

class UpdateTransactionUseCase@Inject constructor(
    private val repository: DataRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        return try {
            repository.updateTransaction(transaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}