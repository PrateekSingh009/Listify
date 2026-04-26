package com.example.listify.domain.repository

import com.example.listify.domain.model.Category
import com.example.listify.domain.model.Cluster
import com.example.listify.domain.model.ClusterWithCategories
import com.example.listify.domain.model.DetectedPayment
import com.example.listify.domain.model.HomeScreenData
import com.example.listify.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface DataRepository {

    // Home screen — single combined flow (2 DB queries under the hood)
    fun getHomeScreenData(): Flow<HomeScreenData>

    // Individual flows (still useful for other screens)
    fun getAllCategories(): Flow<List<Category>>
    fun getClustersWithCategories(): Flow<List<ClusterWithCategories>>
    fun getGeneralCategories(): Flow<List<Category>>
    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>>
    fun getCategoryById(id: Long): Flow<Category>

    suspend fun updateLastUpdated(categoryId: Long, lastUpdated: Long)

    // Cluster writes
    suspend fun insertCluster(cluster: Cluster): Long
    suspend fun insertClusterWithCategory(cluster: Cluster, category: Category)

    // Category writes
    suspend fun insertCategory(category: Category): Long
    suspend fun deleteCategory(category: Category)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategoryAndCleanupCluster(category: Category)
    suspend fun updateTotalPlanned(categoryId: Long, totalPlanned: Double)

    // Transaction writes
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)

    // Detected Payments
    suspend fun saveDetectedPayment(payment: DetectedPayment)
    fun getUnprocessedDetectedPayments(): Flow<List<DetectedPayment>>
    fun getProcessedDetectedPayments(): Flow<List<DetectedPayment>>
    suspend fun markDetectedPaymentAsProcessed(id: Long)
    suspend fun deleteDetectedPayment(detectedPayment: DetectedPayment)
}