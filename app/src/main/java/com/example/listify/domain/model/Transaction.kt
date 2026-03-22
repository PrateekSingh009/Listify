package com.example.listify.domain.model

data class Transaction(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val updatedAt : Long ,
    val categoryId: Long
)