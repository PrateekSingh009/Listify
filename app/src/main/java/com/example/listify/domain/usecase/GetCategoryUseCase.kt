package com.example.listify.domain.usecase

import com.example.listify.domain.model.Category
import com.example.listify.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryUseCase @Inject constructor(private val repository: DataRepository) {
    operator fun invoke(): Flow<List<Category>> = repository.getAllCategories()
}