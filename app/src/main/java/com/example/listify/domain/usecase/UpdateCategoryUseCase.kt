package com.example.listify.domain.usecase

import com.example.listify.domain.model.Category
import com.example.listify.domain.repository.DataRepository
import javax.inject.Inject

class UpdateCategoryUseCase @Inject constructor(
    private val repository: DataRepository
) {
    suspend operator fun invoke(category: Category): Result<Unit> {
        return try {
            repository.updateCategory(category)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}