package com.example.listify.data.mapper

import com.example.listify.data.local.entity.CategoryEntity
import com.example.listify.data.local.entity.ClusterEntity
import com.example.listify.data.local.entity.ClusterWithCategoriesEntity
import com.example.listify.data.local.entity.DetectedPaymentEntity
import com.example.listify.data.local.entity.TransactionEntity
import com.example.listify.domain.model.Category
import com.example.listify.domain.model.Cluster
import com.example.listify.domain.model.ClusterWithCategories
import com.example.listify.domain.model.DetectedPayment
import com.example.listify.domain.model.Transaction

fun TransactionEntity.toDomain() = Transaction(id, title, amount, updatedAt, categoryId)
fun Transaction.toEntity() = TransactionEntity(id, title, amount, updatedAt, categoryId)

//fun CategoryEntity.toDomain() = Category(id, clusterId, title, lastUpdated, totalPlanned)
fun CategoryEntity?.toDomain(): Category = this?.let {
    Category(
        id = it.id,
        clusterId = it.clusterId,
        title = it.title,
        lastUpdated = it.lastUpdated,
        totalPlanned = it.totalPlanned
    )
} ?: Category(0, null, "Unknown", System.currentTimeMillis(), 0.0)
fun Category.toEntity() = CategoryEntity(id, clusterId, title, lastUpdated, totalPlanned)

fun ClusterEntity.toDomain() = Cluster(id, name, createdAt)
fun Cluster.toEntity() = ClusterEntity(id, name, createdAt)

fun ClusterWithCategoriesEntity.toDomain() = ClusterWithCategories(
    cluster = cluster.toDomain(),
    categories = categories.map { it.toDomain() }
)

fun DetectedPaymentEntity.toDomain() = DetectedPayment(
    id = id,
    amount = amount,
    merchant = merchant,
    appName = appName,
    timestamp = timestamp,
    rawText = rawText,
    isProcessed = isProcessed
)

fun DetectedPayment.toEntity() = DetectedPaymentEntity(
    id = id,
    amount = amount,
    merchant = merchant,
    appName = appName,
    timestamp = timestamp,
    rawText = rawText,
    isProcessed = isProcessed
)