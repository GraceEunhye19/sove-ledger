package com.example.sovereignledger.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape


val Shapes = Shapes (
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

object AppShapes{
    val pill = RoundedCornerShape(50)
    val notificationButton = RoundedCornerShape(12.dp)
    val amountCard = RoundedCornerShape(20.dp) //for the amount? will be fixed
    val fab = RoundedCornerShape(16.dp)
    val numpadKey = RoundedCornerShape(12.dp) //reuse for categories or fab shape but bigger
    val progressBar = RoundedCornerShape(50)
    val bottomSheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val cardItem = RoundedCornerShape(16.dp)

}