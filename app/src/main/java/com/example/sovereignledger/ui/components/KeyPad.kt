package com.example.sovereignledger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sovereignledger.ui.theme.AppShapes
import com.example.sovereignledger.ui.theme.SovereignNavy
import com.example.sovereignledger.ui.theme.SurfaceWhite

@Composable
fun KeyPad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit
){
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "delete")

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        keys.chunked(3).forEach {rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowKeys.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.5f)
                            .clip(AppShapes.numpadKey)
                            .background(SurfaceWhite)
                            .clickable { if (key == "delete") onDeleteClick() else onNumberClick(key) },
                        contentAlignment = Alignment.Center){
                        if(key == "delete"){Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = null, tint = SovereignNavy)}
                        else {
                            Text(
                                text = key,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = SovereignNavy
                            )
                        }
                    }
                }
            }

        }
    }
}