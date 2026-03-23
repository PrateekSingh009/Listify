package com.example.listify.domain.usecase

import com.example.listify.domain.model.Category
import com.example.listify.domain.repository.DataRepository
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(private val repository: DataRepository) {
    suspend operator fun invoke(
        title : String,
        clusterId: Long? = null // null → General; non-null → belongs to a cluster
    ): Result<Long> = runCatching {
        require(title.isNotBlank()) {"Title Required"}
        val category = Category(
            title = title,
            clusterId = clusterId,
            lastUpdated = System.currentTimeMillis()
        )
        repository.insertCategory(category)
    }
}
