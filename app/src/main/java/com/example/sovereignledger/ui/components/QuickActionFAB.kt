package com.example.sovereignledger.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.sovereignledger.ui.theme.AppShapes
import com.example.sovereignledger.ui.theme.BackgroundGray
import com.example.sovereignledger.ui.theme.SovereignBlue
import com.example.sovereignledger.ui.theme.SovereignNavy
import com.example.sovereignledger.ui.theme.SurfaceWhite

@Composable
fun QuickAcionFAB(
    expanded: Boolean, //state hoisting
    onToggle: () -> Unit,
    onManualClick: () -> Unit,
    onCaptureClick: () -> Unit,
    onUploadClick: () -> Unit
){
    //var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.padding(16.dp)
    ) {

        AnimatedVisibility(
            visible = expanded,
            enter = slideInHorizontally{it } + fadeIn(),
            exit = slideOutHorizontally {it } + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(SovereignBlue.copy(alpha = 0f), AppShapes.pill)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ){
                FabSubItems(Icons.Default.EditNote, "Manual", onManualClick)
                FabSubItems(Icons.Default.DocumentScanner, "Capture", onCaptureClick)
                FabSubItems(Icons.Default.UploadFile, "Upload", onUploadClick)
            }
        }

        FloatingActionButton(
            onClick = onToggle,
            containerColor = SovereignBlue,
            contentColor = SurfaceWhite,
            shape = AppShapes.fab
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = "Expand Actions"
            )
        }
    }
}

//@Composable
//private fun FabSubItems(
//    icon: ImageVector, label: String, onClick: () -> Unit
//){
//    Icon(icon, contentDescription = label, tint = SovereignBlue)
//}

@Composable
private fun FabSubItems(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
){
    Surface(
        color = Color.White,
        shape = AppShapes.cardItem,
        shadowElevation = 6.dp,
        modifier = Modifier
            .padding(8.dp)
            .size(48.dp) // Distinct button size
            .clickable { onClick() },
        contentColor = SovereignNavy
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = SovereignNavy,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
