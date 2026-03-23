package com.example.listify.data.mapper

import com.example.listify.data.local.entity.CategoryEntity
import com.example.listify.data.local.entity.ClusterEntity
import com.example.listify.data.local.entity.ClusterWithCategoriesEntity
import com.example.listify.data.local.entity.TransactionEntity
import com.example.listify.domain.model.Category
import com.example.listify.domain.model.Cluster
import com.example.listify.domain.model.ClusterWithCategories
import com.example.listify.domain.model.Transaction

fun TransactionEntity.toDomain() = Transaction(id, title, amount, updatedAt,categoryId)
fun Transaction.toEntity() = TransactionEntity(id, title, amount, updatedAt,categoryId)

fun CategoryEntity.toDomain() = Category(id, clusterId,title,lastUpdated)
fun Category.toEntity() = CategoryEntity(id, clusterId,title,lastUpdated)

fun ClusterEntity.toDomain() = Cluster(id, name, createdAt)
fun Cluster.toEntity() = ClusterEntity(id, name, createdAt)

fun ClusterWithCategoriesEntity.toDomain() = ClusterWithCategories(
    cluster = cluster.toDomain(),
    categories = categories.map { it.toDomain() }
)