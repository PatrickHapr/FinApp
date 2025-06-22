package com.example.fintech.data.model

data class Transaction(
    var id: Int = 0,
    val type: String,
    val description: String,
    val value: Double
)