package com.example.listify.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.listify.data.local.entity.CategoryEntity
import com.example.listify.data.local.entity.ClusterEntity
import com.example.listify.data.local.entity.ClusterWithCategoriesEntity
import com.example.listify.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    // ── Cluster ──────────────────────────────────────────────────
    @Transaction
    @Query("SELECT * FROM clusters ORDER BY createdAt DESC")
    abstract fun getClustersWithCategories(): Flow<List<ClusterWithCategoriesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCluster(clusterEntity: ClusterEntity): Long

    // ── Category ─────────────────────────────────────────────────
    @Query("SELECT * FROM categories WHERE clusterId IS NULL ORDER BY lastUpdated DESC")
    abstract fun getGeneralCategories(): Flow<List<CategoryEntity>>

    @Transaction
    @Query("SELECT * FROM categories")
    abstract fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    abstract fun getCategoryById(id: Long): Flow<CategoryEntity>

    @Query("UPDATE categories SET lastUpdated = :lastUpdated WHERE id = :categoryId")
    abstract suspend fun updateLastUpdated(categoryId: Long, lastUpdated: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCategory(categoryEntity: CategoryEntity): Long

    @Delete
    abstract suspend fun deleteCategory(categoryEntity: CategoryEntity)

    // ── Transaction ───────────────────────────────────────────────
    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY updatedAt DESC")
    abstract fun getTransactionsByCategory(categoryId: Long): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTransaction(transactionEntity: TransactionEntity): Long

    @Delete
    abstract suspend fun deleteTransaction(transactionEntity: TransactionEntity)

    // ── Compound (atomic) ─────────────────────────────────────────
    /**
     * Inserts the cluster first, then uses the generated ID to stamp
     * the first category — all in one Room transaction, so no orphaned rows.
     */
    @Transaction
    open suspend fun insertClusterWithCategory(
        cluster: ClusterEntity,
        category: CategoryEntity
    ) {
        val newClusterId = insertCluster(cluster)
        insertCategory(category.copy(clusterId = newClusterId))
    }

}