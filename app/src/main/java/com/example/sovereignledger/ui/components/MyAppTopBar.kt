package com.example.sovereignledger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sovereignledger.ui.theme.AppShapes
import com.example.sovereignledger.ui.theme.SovereignNavy
import com.example.sovereignledger.ui.theme.SurfaceWhite
import com.example.sovereignledger.ui.theme.TextSecondary

@Composable
fun MyAppTopBar(
    title: String = "",
    userName: String = "Alexander Sterling",
    isDashboard: Boolean = true,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        if (isDashboard){
            Row(verticalAlignment = Alignment.CenterVertically){
                Box(modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray)){

                    // Replace with actual Coil ImageRequest later
                    Text("AS", modifier = Modifier.align(Alignment.Center), color = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Sovereign Ledger",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = userName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SovereignNavy
                    )
                }
            }
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(45.dp)
                    .clip(AppShapes.notificationButton)
                    .background(SurfaceWhite)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = SovereignNavy
                )
            }
        } else { //back button + title of page
            Row(verticalAlignment = Alignment.CenterVertically){
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SovereignNavy)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SovereignNavy
                )
            }
        }
    }
}