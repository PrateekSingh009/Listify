package com.example.listify.data.repository

import com.example.listify.data.local.dao.Dao
import com.example.listify.data.local.dao.DetectedPaymentDao
import com.example.listify.data.mapper.toDomain
import com.example.listify.data.mapper.toEntity
import com.example.listify.domain.model.Category
import com.example.listify.domain.model.Cluster
import com.example.listify.domain.model.ClusterWithCategories
import com.example.listify.domain.model.DetectedPayment
import com.example.listify.domain.model.HomeScreenData
import com.example.listify.domain.model.Transaction
import com.example.listify.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    private val dao: Dao,
    private val detectedPaymentDao: DetectedPaymentDao
) : DataRepository {
    /**
     * Combines 2 Room flows into a single HomeScreenData emission.
     * Room re-emits each flow independently whenever its tables change,
     * and [combine] merges them — so the UI always gets fresh data with
     * no extra DB round-trips.
     */
    override fun getHomeScreenData(): Flow<HomeScreenData> = combine(
        dao.getClustersWithCategories().map { list -> list.map { it.toDomain() } },
        dao.getGeneralCategories().map { list -> list.map { it.toDomain() } }
    ) { clustered, general ->
        HomeScreenData(clustered, general)
    }

    override fun getAllCategories(): Flow<List<Category>> =
        dao.getAllCategories().map { list -> list.map { it.toDomain() } }

    override fun getClustersWithCategories(): Flow<List<ClusterWithCategories>> =
        dao.getClustersWithCategories().map { list -> list.map { it.toDomain() } }

    override fun getGeneralCategories(): Flow<List<Category>> =
        dao.getGeneralCategories().map { list -> list.map { it.toDomain() } }

    override fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> =
        dao.getTransactionsByCategory(categoryId).map { it.map { entity -> entity.toDomain() } }

    override fun getCategoryById(id: Long): Flow<Category> =
        dao.getCategoryById(id).map { it.toDomain() }

    override suspend fun updateLastUpdated(categoryId: Long, lastUpdated: Long) =
        dao.updateLastUpdated(categoryId, lastUpdated)

    override suspend fun insertCluster(cluster: Cluster): Long =
        dao.insertCluster(cluster.toEntity())

    override suspend fun insertClusterWithCategory(cluster: Cluster, category: Category) =
        dao.insertClusterWithCategory(cluster.toEntity(), category.toEntity())

    override suspend fun insertCategory(category: Category): Long =
        dao.insertCategory(category.toEntity())

    override suspend fun deleteCategory(category: Category) =
        dao.deleteCategory(category.toEntity())

    override suspend fun updateCategory(category: Category) {
        dao.updateCategory(category.toEntity())
    }

    override suspend fun deleteCategoryAndCleanupCluster(category: Category) {
        dao.deleteCategory(category.toEntity())

        category.clusterId?.let { clusterId ->
            val remainingCount = dao.getCategoryCountInCluster(clusterId)
            if (remainingCount == 0) {
                dao.deleteCluster(Cluster(id = clusterId, name = "", createdAt = 0).toEntity())
            }
        }
    }

    override suspend fun updateTotalPlanned(categoryId: Long, totalPlanned: Double) {
        dao.updateTotalPlanned(categoryId, totalPlanned)
    }

    override suspend fun insertTransaction(transaction: Transaction): Long =
        dao.insertTransaction(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) {
        dao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) =
        dao.deleteTransaction(transaction.toEntity())

    /*Detected Payment*/
    override suspend fun saveDetectedPayment(payment: DetectedPayment) {
        detectedPaymentDao.insert(payment.toEntity())
    }

    override fun getUnprocessedDetectedPayments(): Flow<List<DetectedPayment>> {
        return detectedPaymentDao.getUnprocessedPayments()
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getProcessedDetectedPayments(): Flow<List<DetectedPayment>> =
        detectedPaymentDao.getProcessedPayments()
            .map { list -> list.map { it.toDomain() } }


    override suspend fun markDetectedPaymentAsProcessed(id: Long) {
        detectedPaymentDao.markAsProcessed(id)
    }

    override suspend fun deleteDetectedPayment(detectedPayment: DetectedPayment) {
        detectedPaymentDao.deleteDetectedPayment(detectedPayment.toEntity())
    }

}