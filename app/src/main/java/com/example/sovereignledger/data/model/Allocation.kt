package com.example.sovereignledger.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Allocation(
    val id: Int,
    val title: String,
    val totalBudget: Double,
    val amountSpent: Double,
    val icon: ImageVector,
    val timeframe: String = "Monthly",
    val isRecurring: Boolean = false,
    val thresholdAlert: Boolean = false,
    val notes: String = ""
){
    val amountLeft: Double get() = totalBudget - amountSpent
    val progress: Double get() = (amountSpent / totalBudget).coerceIn(0.0, 1.0)
}
