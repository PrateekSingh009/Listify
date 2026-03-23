package com.example.listify.domain.model

data class HomeScreenData(
    val clusteredSections: List<ClusterWithCategories>,
    val generalCategories: List<Category>
)