package com.example.listify.domain.usecase

import com.example.listify.domain.model.Transaction
import com.example.listify.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionListUseCase @Inject constructor(private val repository: DataRepository) {
    operator fun invoke(categoryId: Long): Flow<List<Transaction>> = repository.getTransactionsByCategory(categoryId)
}