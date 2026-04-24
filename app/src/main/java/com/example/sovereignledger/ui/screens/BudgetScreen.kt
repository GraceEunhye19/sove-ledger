package com.example.sovereignledger.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sovereignledger.data.model.Allocation
import com.example.sovereignledger.data.model.Transaction
import com.example.sovereignledger.ui.components.AppButton
import com.example.sovereignledger.ui.components.EmptyStateContent
import com.example.sovereignledger.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BudgetScreen(
    allocations: List<Allocation>,
    transactions: List<Transaction>,
    categories: List<CategoryOption>,
    onAddCategory: (CategoryOption) -> Unit
) {
    var showNewCategoryForm by remember { mutableStateOf(false) }


    val totalBudget = allocations.sumOf { it.totalBudget }
    val totalSpent = allocations.sumOf { it.amountSpent }
    val budgetRemaining = totalBudget - totalSpent
    val burnPercent = if (totalBudget > 0) (totalSpent / totalBudget * 100).toInt() else 0
    val isOnTrack = totalSpent <= totalBudget * 0.8

    // Daily spending velocity — last 7 days
    val dailyTotals = remember(transactions) { getDailyTotals(transactions) }

    // Categories with transactions
    val activeCategories = remember(transactions, allocations) {
        allocations.filter { alloc ->
            transactions.any { it.allocationId == alloc.id && it.isExpense }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundGray)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            MonthlyBurnCard(
                totalSpent = totalSpent,
                totalBudget = totalBudget,
                burnPercent = burnPercent,
                budgetRemaining = budgetRemaining,
                isOnTrack = isOnTrack
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(title = "Spending Velocity")
            SpendingVelocityChart(dailyTotals = dailyTotals)

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(title = "Categories")
            CategoryScrollRow(
                categories = categories,
                onAddClick = { showNewCategoryForm = true }
            )

            Spacer(modifier = Modifier.height(24.dp))


            if (activeCategories.isEmpty()) {
                EmptyStateContent(
                    icon = Icons.Default.GridView,
                    title = "No active categories",
                    subtitle = "",
                    buttonLabel = "Add Transaction",
                    onQuickAdd = {showNewCategoryForm = true}
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    activeCategories.forEach { alloc ->
                        BudgetCategoryCard(allocation = alloc)
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }


        AnimatedVisibility(
            visible = showNewCategoryForm,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.fillMaxSize()
        ) {
            NewCategoryForm(
                onSave = { cat ->
                    onAddCategory(cat)
                    showNewCategoryForm = false
                },
                onDismiss = { showNewCategoryForm = false }
            )
        }
    }
}


@Composable
fun MonthlyBurnCard(
    totalSpent: Double,
    totalBudget: Double,
    burnPercent: Int,
    budgetRemaining: Double,
    isOnTrack: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd)))
                .padding(20.dp)
        ) {

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        if (isOnTrack) Color(0x972E7D52) else Color(0x97C0392B),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isOnTrack) "ON TRACK" else "OVER BUDGET",
                    color = if (isOnTrack) SuccessGreen else ErrorRed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    "MONTHLY BURN",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "$${String.format("%.2f", totalSpent)}",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { (totalSpent / totalBudget.coerceAtLeast(1.0)).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(100.dp)),
                    color = if (isOnTrack) SuccessGreen else ErrorRed,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "$burnPercent% of $${String.format("%.2f", totalBudget)} used",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    Text(
                        "$${String.format("%.2f", budgetRemaining)} left",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}


data class DayTotal(val label: String, val amount: Double)

fun getDailyTotals(transactions: List<Transaction>): List<DayTotal> {
    val dayLabels = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
    val cal = Calendar.getInstance()

    return (6 downTo 0).mapIndexed { index, daysAgo ->
        cal.time = Date()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val dayStr = sdf.format(cal.time)
        val total = transactions
            .filter { it.isExpense && it.date == dayStr }
            .sumOf { it.amount }
        DayTotal(label = dayLabels[index], amount = total)
    }
}

@Composable
fun SpendingVelocityChart(dailyTotals: List<DayTotal>) {
    val maxAmount = dailyTotals.maxOfOrNull { it.amount }?.coerceAtLeast(1.0) ?: 1.0


    val avg = dailyTotals.map { it.amount }.average()
    val latest = dailyTotals.lastOrNull()?.amount ?: 0.0
    val velocityPercent = if (avg > 0) ((latest - avg) / avg * 100).toInt() else 0
    val isPositive = velocityPercent >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Trend relative to baseline", fontSize = 12.sp, color = TextSecondary)
                Box(
                    modifier = Modifier
                        .background(
                            if (isPositive) ErrorRed.copy(alpha = 0.1f) else SuccessGreen.copy(alpha = 0.1f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        "${if (isPositive) "+" else ""}$velocityPercent%",
                        color = if (isPositive) ErrorRed else SuccessGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bar chart
            Row(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                dailyTotals.forEach { day ->
                    val heightFraction = (day.amount / maxAmount).toFloat().coerceIn(0.05f, 1f)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .fillMaxHeight(heightFraction)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(SovereignBlue, SovereignBlue.copy(alpha = 0.4f))
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(day.label, fontSize = 9.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryScrollRow(
    categories: List<CategoryOption>,
    onAddClick: () -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(categories) { cat ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceWhite)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SovereignBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(cat.icon, contentDescription = null,
                        tint = SovereignNavy, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(cat.label, fontSize = 10.sp, color = SovereignNavy, fontWeight = FontWeight.Medium)
            }
        }


        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SovereignBlue.copy(alpha = 0.08f))
                    .clickable { onAddClick() }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SovereignBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Category",
                        tint = SovereignBlue, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("New", fontSize = 10.sp, color = SovereignBlue, fontWeight = FontWeight.Medium)
            }
        }
    }
}


@Composable
fun BudgetCategoryCard(allocation: Allocation) {
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
            modifier = Modifier.size(44.dp).clip(CircleShape).background(BackgroundGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(allocation.icon, contentDescription = null,
                tint = SovereignNavy, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(allocation.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "$${String.format("%.2f", allocation.totalBudget)}",
                fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SovereignNavy
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { allocation.progress.toFloat() },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(AppShapes.progressBar),
                color = indicatorColor,
                trackColor = BackgroundGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "$${String.format("%.2f", allocation.amountLeft)} LEFT",
                fontSize = 12.sp, color = textIndicatorColor, fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun NewCategoryForm(
    onSave: (CategoryOption) -> Unit,
    onDismiss: () -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(Icons.Default.MoreHoriz as ImageVector) }

    val iconOptions = listOf(
        Icons.Default.Restaurant, Icons.Default.Flight, Icons.Default.Money,
        Icons.Default.ShoppingCart, Icons.Default.Home, Icons.Default.FitnessCenter,
        Icons.Default.LocalGasStation, Icons.Default.TheaterComedy, Icons.Default.MoreHoriz
    )

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundGray).padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        FormLabel("Category Name")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceWhite)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Person, contentDescription = null,
                tint = TextSecondary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (categoryName.isEmpty()) Text("John Doe", color = Color.LightGray, fontSize = 14.sp)
                    inner()
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        FormLabel("Choose Icon")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            iconOptions.take(5).forEach { icon ->
                val isSelected = selectedIcon == icon
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) SovereignBlue.copy(alpha = 0.15f) else SurfaceWhite
                        )
                        .clickable { selectedIcon = icon },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null,
                        tint = if (isSelected) SovereignBlue else TextSecondary,
                        modifier = Modifier.size(20.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            iconOptions.drop(5).forEach { icon ->
                val isSelected = selectedIcon == icon
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) SovereignBlue.copy(alpha = 0.15f) else SurfaceWhite
                        )
                        .clickable { selectedIcon = icon },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null,
                        tint = if (isSelected) SovereignBlue else TextSecondary,
                        modifier = Modifier.size(20.dp))
                }
            }
            // fill remaining space if less than 5 icons in second row
            repeat(5 - iconOptions.drop(5).size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AppButton(
            text = "Save Up!",
            onClick = {
                if (categoryName.isNotBlank()) {
                    onSave(CategoryOption(label = categoryName.trim(), icon = selectedIcon))
                }
            }
        )
    }
}