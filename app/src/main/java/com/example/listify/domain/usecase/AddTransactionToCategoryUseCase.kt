package com.example.listify.domain.usecase

import com.example.listify.domain.model.Transaction
import com.example.listify.domain.repository.DataRepository
import javax.inject.Inject

class AddTransactionToCategoryUseCase @Inject constructor(
    private val repository: DataRepository
) {
    suspend operator fun invoke(title: String, amount: Double, categoryId: Long): Result<Unit> {
        val transaction = Transaction(
            id = 0,
            title = title,
            amount = amount,
            updatedAt = System.currentTimeMillis(),
            categoryId = categoryId
        )
        return runCatching { repository.insertTransaction(transaction) }
    }
}