package com.example.listify.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ClusterWithCategoriesEntity(
    @Embedded val cluster: ClusterEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "clusterId"
    )
    val categories: List<CategoryEntity>
)