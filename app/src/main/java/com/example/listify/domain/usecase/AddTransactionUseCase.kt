package com.example.listify.domain.usecase

import com.example.listify.domain.model.Transaction
import com.example.listify.domain.repository.DataRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(private val repository: DataRepository) {
    suspend operator fun invoke(
        title : String,
        amount : Double,
        categoryId : Long
    ): Result<Long> = runCatching {

        require(title.isNotBlank()) {"Title Required"}
        require(amount.isFinite()) {"Amount Required"}

        val transaction = Transaction(
            title = title,
            amount = amount,
            updatedAt = System.currentTimeMillis(),
            categoryId = categoryId
        )
        repository.updateLastUpdated(categoryId = categoryId, lastUpdated = System.currentTimeMillis())
        repository.insertTransaction(transaction)
    }
}

