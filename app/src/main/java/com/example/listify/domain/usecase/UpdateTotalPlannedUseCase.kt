package com.example.listify.domain.usecase

import com.example.listify.domain.repository.DataRepository
import javax.inject.Inject

class UpdateTotalPlannedUseCase @Inject constructor(
    private val repository: DataRepository
) {
    suspend operator fun invoke(categoryId: Long, totalPlanned: Double): Result<Unit> =
        runCatching {
            repository.updateTotalPlanned(categoryId, totalPlanned)
        }
}