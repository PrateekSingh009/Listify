package com.example.listify.domain.model

data class ClusterWithCategories(
    val cluster: Cluster,
    val categories: List<Category>
)