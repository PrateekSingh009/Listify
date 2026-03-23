package com.example.listify.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listify.domain.model.Category
import com.example.listify.domain.model.HomeScreenData
import com.example.listify.domain.usecase.AddCategoryUseCase
import com.example.listify.domain.usecase.AddClusterUseCase
import com.example.listify.domain.usecase.DeleteCategoryUseCase
import com.example.listify.domain.usecase.GetHomeScreenDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val addClusterUseCase: AddClusterUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val homeScreenData: StateFlow<HomeScreenData> = getHomeScreenDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeScreenData(emptyList(), emptyList())
        )

    /** Adds a general category (clusterId = null) or one inside an existing cluster. */
    fun addCategory(title: String, clusterId: Long? = null) {
        viewModelScope.launch {
            addCategoryUseCase(title, clusterId)
                .onSuccess { _error.value = null }
                .onFailure { _error.value = it.message }
        }
    }

    /** Creates a new cluster + its first category in one atomic write. */
    fun addCluster(clusterName: String, firstCategoryName: String) {
        viewModelScope.launch {
            addClusterUseCase(clusterName, firstCategoryName)
                .onSuccess { _error.value = null }
                .onFailure { _error.value = it.message }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            deleteCategoryUseCase(category).onFailure {
                _error.value = "Could not delete category"
            }
        }
    }
}