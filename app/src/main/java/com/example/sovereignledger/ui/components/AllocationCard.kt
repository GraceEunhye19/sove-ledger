package com.example.sovereignledger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sovereignledger.ui.theme.*

@Composable
fun AllocationCard(
    title: String,
    amountLeft: String,
    totalBudget: String,
    progress: Double,
    icon: ImageVector
){
    val indicatorColor = if (progress >= 0.8f) ErrorRed else SovereignBlue
    val textIndicatorColor = if (progress >= 0.8f) ErrorRed else TextSecondary
    Column(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .fillMaxWidth()
            .clip(AppShapes.cardItem)
            .background(SurfaceWhite)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(BackgroundGray.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ){
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SovereignNavy,
                modifier = Modifier.size(20.dp)
            )
        }

        Column{
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = totalBudget,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SovereignNavy
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress.toFloat() }, // Current progress value
                modifier = Modifier
                    .fillMaxWidth() // Span full internal width
                    .height(6.dp) // Vertical height of the bar
                    .clip(AppShapes.progressBar), // Use our fully rounded "pill" shape
                color = indicatorColor, // Blue for filled section
                trackColor = BackgroundGray // Grey for empty track
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = amountLeft,
                fontSize = 12.sp,
                color = textIndicatorColor,
                textAlign = TextAlign.Start
            )
        }
    }
}

//@Preview
//@Composable
//fun Preview(){
//    AllocationCard(
//        title = "food",
//        amountLeft = "200",
//        totalBudget = "300",
//        progress = 0.0f,
//        icon = Icons.Default.Restaurant
//    )
//}