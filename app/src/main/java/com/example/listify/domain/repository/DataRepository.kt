package com.example.listify.domain.repository

import com.example.listify.domain.model.Category
import com.example.listify.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    fun getAllCategories(): Flow<List<Category>>
    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>>
    fun getCategoryById(id: Long): Flow<Category>
    suspend fun updateLastUpdated(categoryId: Long, lastUpdated: Long)
    suspend fun insertCategory(category: Category): Long
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun deleteCategory(category: Category)
    suspend fun deleteTransaction(transaction: Transaction)
}