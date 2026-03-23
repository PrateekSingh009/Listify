package com.example.listify.domain.usecase

import com.example.listify.domain.model.HomeScreenData
import com.example.listify.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHomeScreenDataUseCase @Inject constructor(private val repository: DataRepository) {
    operator fun invoke(): Flow<HomeScreenData> = repository.getHomeScreenData()
}