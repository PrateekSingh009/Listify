package com.example.listify.domain.model

data class Category(
    val id: Long = 0,
    val clusterId : Long?,
    val title: String,
    val lastUpdated : Long
)