package com.example.sovereignledger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sovereignledger.ui.theme.AppShapes.bottomSheet
import com.example.sovereignledger.ui.theme.SovereignBlue
import com.example.sovereignledger.ui.theme.TextSecondary

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
){
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.White,
        //tonalElevation = 8.dp,
        modifier = Modifier
            .clip(bottomSheet)
            .shadow(elevation = 12.dp, shape = bottomSheet)
    ) {
        val items = listOf(
            NavigationItem("Overview", "overview", Icons.Default.GridView),
            NavigationItem("Budgets", "budgets", Icons.Default.AccountBalanceWallet),
            NavigationItem("Insights", "insights", Icons.Default.Insights),
            NavigationItem("Settings", "settings", Icons.Default.Settings))

        items.forEach { item ->

            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                label = { Text(item.title, fontSize = 10.sp) },
                icon = {
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selected) SovereignBlue.copy(alpha = 0.12f)
                            else Color.Transparent
                        )
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center) {
                        Icon(item.icon, contentDescription = item.title)
                    }
                       },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SovereignBlue,
                    selectedTextColor = SovereignBlue,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Color.Transparent)
            )
        }
    }
}

data class NavigationItem(val title: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)