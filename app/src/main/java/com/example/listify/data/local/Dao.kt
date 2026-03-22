package com.example.listify.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.listify.data.local.entity.CategoryEntity
import com.example.listify.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Transaction
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY updatedAt DESC")
    fun getTransactionsByCategory(categoryId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryById(id: Long): Flow<CategoryEntity>

    @Query("UPDATE categories SET lastUpdated = :lastUpdated WHERE id = :categoryId")
    suspend fun updateLastUpdated(categoryId: Long, lastUpdated: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categoryEntity: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transactionEntity: TransactionEntity): Long

    @Delete
    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    @Delete
    suspend fun deleteTransaction(transactionEntity: TransactionEntity)

}