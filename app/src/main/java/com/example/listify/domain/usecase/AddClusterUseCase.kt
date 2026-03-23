package com.example.listify.domain.usecase

import com.example.listify.domain.model.Category
import com.example.listify.domain.model.Cluster
import com.example.listify.domain.repository.DataRepository
import javax.inject.Inject

class AddClusterUseCase @Inject constructor(private val repository: DataRepository) {
    /**
     * Creates a cluster + its first category in a single atomic DB transaction.
     * The category's clusterId is assigned inside the DAO after the cluster row
     * is inserted, so we never have to hard-code an ID here.
     */
    suspend operator fun invoke(
        clusterName: String,
        firstCategoryName: String
    ): Result<Unit> = runCatching {
        require(clusterName.isNotBlank()) { "Cluster name required" }
        require(firstCategoryName.isNotBlank()) { "First category name required" }

        val cluster = Cluster(
            name = clusterName,
            createdAt = System.currentTimeMillis()
        )
        // clusterId placeholder — overwritten atomically inside Dao.insertClusterWithCategory
        val category = Category(
            title = firstCategoryName,
            clusterId = null,
            lastUpdated = System.currentTimeMillis()
        )
        repository.insertClusterWithCategory(cluster, category)
    }
}