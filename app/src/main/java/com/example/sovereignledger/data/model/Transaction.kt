package com.example.sovereignledger.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Transaction(
    val id: Int,
    val title: String,
    val category: String,
    val amount: Double,
    val time: String,
    val date: String,
    val icon: ImageVector,
    val isExpense: Boolean = true,
    val allocationId: Int? = null, // links to an allocation
    val notes: String = ""
)
