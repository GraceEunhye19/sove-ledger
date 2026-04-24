package com.example.sovereignledger.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddCard
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sovereignledger.data.model.Allocation
import com.example.sovereignledger.ui.components.AppButton
import com.example.sovereignledger.ui.components.KeyPad
import com.example.sovereignledger.ui.components.AllocationCard
import com.example.sovereignledger.ui.components.EmptyStateContent
import com.example.sovereignledger.ui.theme.*
//catergory classes, which we'll be able to add to in the future
//allocation or budget
data class CategoryOption(val label: String, val icon: ImageVector)

val allCategories = listOf(
    CategoryOption("Food",      Icons.Default.Restaurant),
    CategoryOption("Travel",    Icons.Default.Flight),
    CategoryOption("Salary",    Icons.Default.Money),
    CategoryOption("Shop",      Icons.Default.ShoppingCart),
    CategoryOption("Home",      Icons.Default.Home),
    CategoryOption("Other",     Icons.Default.MoreHoriz)
)

@Composable
fun AllocationListScreen(
    allocations: List<Allocation>,
    onAddAllocation: (Allocation) -> Unit
) {
    var showNewForm by remember { mutableStateOf(false) } //no allocations
    var nextId by remember { mutableIntStateOf(allocations.size + 1) } //add to list

    Box(
        modifier = Modifier.fillMaxSize().background(BackgroundGray)
    ) {

        if (allocations.isEmpty() && !showNewForm) {
            EmptyStateContent(
                icon = Icons.Outlined.AddCard,
                title = "New Allocation",
                subtitle = "Record a new Allocation",
                onQuickAdd = { showNewForm = true })
        } else if (!showNewForm) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
            ) {
                items(allocations) { allocation ->
                    AllocationListCard(allocation = allocation)
                }

                item {
                    EmptyStateContent(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        icon = Icons.Outlined.AddCard,
                        title = "New Allocation",
                        subtitle = "Record a new Allocation",
                        onQuickAdd = { showNewForm = true }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showNewForm,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.fillMaxSize()
        ) {
            NewAllocationForm(
                onSave = { allocation ->
                    onAddAllocation(allocation.copy(id = nextId++))
                    showNewForm = false
                },
                onDismiss = { showNewForm = false }
            )
        }
    }

}


@Composable
fun AllocationListCard(allocation: Allocation){
    val indicatorColor = if (allocation.progress >= 0.8f) ErrorRed else SovereignBlue
    val textIndicatorColor = if (allocation.progress >= 0.8f) ErrorRed else TextSecondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.cardItem)
            .background(SurfaceWhite)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(BackgroundGray),
            contentAlignment = Alignment.Center
        ){
            Icon(allocation.icon, contentDescription = null, tint = SovereignNavy, modifier = Modifier.size(20.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(allocation.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text("$${String.format("%.2f", allocation.totalBudget)}", fontWeight = FontWeight.Bold,
                fontSize = 18.sp, color = SovereignNavy)
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { allocation.progress.toFloat() },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(AppShapes.progressBar),
                color = indicatorColor,
                trackColor = BackgroundGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "$${String.format("%.2f", allocation.amountLeft)} left",
                fontSize = 12.sp, color = textIndicatorColor
            )
        }
    }
}

@Composable
fun NewAllocationForm(
    onSave: (Allocation) -> Unit,
    onDismiss: () -> Unit
){
    var amountDisplay by remember { mutableStateOf("0.00") }
    var selectedCategory by remember { mutableStateOf<CategoryOption?>(null) }
    var selectedTimeframe by remember { mutableStateOf("Monthly") }
    var isRecurring by remember { mutableStateOf(false) }
    var thresholdEnabled by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var showKeypad by remember { mutableStateOf(false) }
    var rawAmount by remember { mutableStateOf("") }

    fun handleNumber(key: String) {
        if (key == "." && rawAmount.contains(".")) return
        if (rawAmount.length >= 10) return
        rawAmount += key
        val double = rawAmount.toDoubleOrNull() ?: 0.0
        amountDisplay = String.format("%.2f", double)
    }

    fun handleDelete() {
        if (rawAmount.isNotEmpty()) {
            rawAmount = rawAmount.dropLast(1)
            val double = rawAmount.toDoubleOrNull() ?: 0.0
            amountDisplay = String.format("%.2f", double)
        } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        Box(
            modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
            .padding(24.dp)
            .clickable { showKeypad = true },
            contentAlignment = Alignment.Center
        ){
            Row(
                verticalAlignment = Alignment.Top
            ){
                Text(
                    "$",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = SovereignNavy,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    //a string gotten from the keypad screen
                    amountDisplay,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SovereignNavy
                )
            }
        }

        Column(modifier = Modifier
            .weight(1f)
            .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            FormLabel("Category")
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                allCategories.forEach { cat ->
                    val isSelected = selectedCategory?.label == cat.label
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) SovereignBlue.copy(alpha = 0.1f) else SurfaceWhite)
                            .border(
                                1.dp,
                                if (isSelected) SovereignBlue else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 8.dp),
                    ) {
                        Icon(cat.icon, contentDescription = null,
                            tint = if (isSelected) SovereignBlue else TextSecondary,
                            modifier = Modifier.size(20.dp))
                        Text(cat.label, fontSize = 9.sp,
                            color = if (isSelected) SovereignBlue else TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            FormLabel("Timeframe")
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Daily", "Weekly", "Monthly").forEach { tf ->
                    val isSelected = selectedTimeframe == tf
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) SovereignBlue else SurfaceWhite)
                            .clickable { selectedTimeframe = tf }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(tf, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ToggleRow(
                icon = Icons.Default.Refresh,
                label = "Recurring Transaction",
                checked = isRecurring,
                onCheckedChange = { isRecurring = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ToggleRow(
                icon = Icons.Default.Notifications,
                label = "Threshold Alert — Notify at 80%",
                checked = thresholdEnabled,
                onCheckedChange = { thresholdEnabled = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormLabel("Notes")
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceWhite)
                    .padding(14.dp)
            ) {
                BasicTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (notes.isEmpty()) Text("What was this for?", color = Color.LightGray, fontSize = 14.sp)
                        inner()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                text = "Save Up!",
                onClick = {
                    val amount = rawAmount.toDoubleOrNull() ?: 0.0
                    if (amount > 0.0 && selectedCategory != null) {
                        onSave(
                            Allocation(
                                id = 0,
                                title = selectedCategory!!.label,
                                totalBudget = amount,
                                amountSpent = 0.0,
                                icon = selectedCategory!!.icon,
                                timeframe = selectedTimeframe,
                                isRecurring = isRecurring,
                                thresholdAlert = thresholdEnabled,
                                notes = notes))
                    }

                }
            )

        }

        AnimatedVisibility(
            visible = showKeypad,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundGray)
                .padding(16.dp)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showKeypad = false }) {
                        Text("Done", color = SovereignBlue, fontWeight = FontWeight.Bold)
                    }}
                KeyPad(
                    onNumberClick = { handleNumber(it) },
                    onDeleteClick = { handleDelete() }
                )
            }
        }


    }
}

@Composable
fun FormLabel(text: String) {
    Text(text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SovereignNavy)
}

@Composable
fun ToggleRow(icon: ImageVector, label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceWhite)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = SovereignNavy, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(label, fontSize = 13.sp, color = SovereignNavy, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SovereignBlue)
        )
    }
}
