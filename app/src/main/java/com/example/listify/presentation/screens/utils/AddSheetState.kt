package com.example.listify.presentation.screens.utils

import com.example.listify.domain.model.Category

sealed class AddSheetState {
    object Hidden : AddSheetState()
    object Choosing : AddSheetState()
    object AddingGeneral : AddSheetState()
    object AddingCluster : AddSheetState()
    data class AddingToCluster(val clusterId: Long, val clusterName: String) : AddSheetState()
    data class EditingCategory(val category: Category) : AddSheetState()
}