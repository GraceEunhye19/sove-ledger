package com.example.sovereignledger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sovereignledger.ui.theme.AppShapes
import com.example.sovereignledger.ui.theme.BackgroundGray
import com.example.sovereignledger.ui.theme.ErrorRed
import com.example.sovereignledger.ui.theme.SovereignNavy
import com.example.sovereignledger.ui.theme.SuccessGreen
import com.example.sovereignledger.ui.theme.TextSecondary

@Composable
fun TransactionCard(
    title: String,
    category: String,
    amount: String,
    time: String,
    date: String,
    icon: ImageVector,
    isExpense: Boolean = true
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(AppShapes.cardItem)
                .background(BackgroundGray),
            contentAlignment = Alignment.Center
        ){
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SovereignNavy,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title.uppercase(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = SovereignNavy
            )
            Text(
                text = "$category • $time",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        Column(horizontalAlignment = Alignment.End
        ) {
            Text(
                text = amount,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isExpense) ErrorRed else SuccessGreen
            )
            Text(
                text = date,
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview (showBackground = true)
@Composable
fun CardPreview(){
    TransactionCard(
        title = "food",
        category = "food",
        amount = "-300",
        time = "11:23pm",
        date = "Oct 23",
        icon = Icons.Default.Restaurant,
        isExpense = true
    )
}