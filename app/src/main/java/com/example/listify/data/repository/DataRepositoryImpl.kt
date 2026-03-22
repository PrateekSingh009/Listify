package com.example.listify.data.repository

import com.example.listify.data.local.Dao
import com.example.listify.data.mapper.toDomain
import com.example.listify.data.mapper.toEntity
import com.example.listify.domain.model.Category
import com.example.listify.domain.model.Transaction
import com.example.listify.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    private val dao: Dao
) : DataRepository {
    override fun getAllCategories(): Flow<List<Category>> =
        dao.getAllCategories().map { list -> list.map { it.toDomain() } }

    override fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> =
        dao.getTransactionsByCategory(categoryId).map { it.map { entity -> entity.toDomain() } }

    override fun getCategoryById(id: Long): Flow<Category> =
        dao.getCategoryById(id).map { it.toDomain() }

    override suspend fun updateLastUpdated(categoryId: Long, lastUpdated: Long) =
        dao.updateLastUpdated(categoryId,lastUpdated)


    override suspend fun insertCategory(category: Category): Long =
        dao.insertCategory(category.toEntity())

    override suspend fun insertTransaction(transaction: Transaction): Long =
        dao.insertTransaction(transaction.toEntity())

    override suspend fun deleteCategory(category: Category) =
        dao.deleteCategory(category.toEntity())

    override suspend fun deleteTransaction(transaction: Transaction) =
        dao.deleteTransaction(transaction.toEntity())

}