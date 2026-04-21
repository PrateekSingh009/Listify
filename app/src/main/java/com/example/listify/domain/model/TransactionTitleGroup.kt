package com.example.listify.domain.model

data class TransactionTitleGroup(
    val title: String,
    val total: Double,
    val count: Int,
    val transactions: List<Transaction>
)
