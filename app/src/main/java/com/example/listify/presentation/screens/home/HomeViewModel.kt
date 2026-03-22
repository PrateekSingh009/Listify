package com.example.listify.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listify.domain.model.Category
import com.example.listify.domain.usecase.AddCategoryUseCase
import com.example.listify.domain.usecase.DeleteCategoryUseCase
import com.example.listify.domain.usecase.GetCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoryUseCase: GetCategoryUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
): ViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val categories: StateFlow<List<Category>> = getCategoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    fun addCategory(title : String) {
        viewModelScope.launch {
            addCategoryUseCase(title)
                .onSuccess {
                    _error.value = null
                }
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