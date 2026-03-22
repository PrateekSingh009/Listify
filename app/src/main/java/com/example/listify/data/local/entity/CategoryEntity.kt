package com.example.listify.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = ClusterEntity::class,
            parentColumns = ["id"],
            childColumns = ["clusterId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clusterId: Long?,
    val title: String,
    val lastUpdated: Long
)