package com.example.sovereignledger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sovereignledger.data.model.Allocation
import com.example.sovereignledger.data.model.Transaction
import com.example.sovereignledger.ui.components.AllocationCard
import com.example.sovereignledger.ui.components.SpendingTrendChart
import com.example.sovereignledger.ui.components.TransactionCard
import com.example.sovereignledger.ui.theme.BackgroundGray
import com.example.sovereignledger.ui.theme.BlueGradientEnd
import com.example.sovereignledger.ui.theme.BlueGradientStart
import com.example.sovereignledger.ui.theme.SovereignBlue
import com.example.sovereignledger.ui.theme.SuccessGreen

@Composable
fun OverviewScreen(
    isDimmed : Boolean,
    allocations: List<Allocation>,
    onViewAllAllocations: () -> Unit,
    transactions: List<Transaction>,
    onViewAllLedger: () -> Unit
){

    val mainScrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            WalletBalanceCard()
            Spacer(modifier = Modifier.height(32.dp))


            SectionHeader(title = "Allocations", onViewAll = onViewAllAllocations)

            AllocationRowSection(allocations = allocations)

            Spacer(modifier = Modifier.height(32.dp))


            SpendingTrendChart(transactions= transactions)

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader(title = "Recent Ledger", onViewAll = onViewAllLedger)
            RecentLedgerSection(
                transactions = transactions.takeLast(5).reversed(),
                onViewAll = onViewAllLedger
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
        if (isDimmed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
        }
    }
}


@Composable
fun WalletBalanceCard(
    balance: String = "$42,950.40",
    percentage: String = "+12.5%",
    onDepositClick: () -> Unit = {},
    onWithdrawClick: () -> Unit = {}
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(BlueGradientStart, BlueGradientEnd)
                    )
                )
                .padding(20.dp)
        ){
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        color = Color(0x972E7D52),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = percentage,
                    color = SuccessGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "LIQUID WEALTH PORTFOLIO",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = balance,
                        style = MaterialTheme.typography.displayLarge,
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold

                    )
                    Text(
                        text = "Market valuation as of today",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onDepositClick,
                        modifier = Modifier
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("DEPOSIT", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold,)
                    }
                    Button(
                        onClick = onWithdrawClick,
                        modifier = Modifier
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("WITHDRAW", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold,)
                    }
                }
            }
        }
    }
}


@Composable

fun SectionHeader(title: String, onViewAll: (()-> Unit)? = null){
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        if (onViewAll != null) {
            TextButton(onClick = onViewAll) {
                Text("View All", fontSize = 12.sp, color = SovereignBlue, fontWeight = FontWeight.Medium)
            }
        } else {
            Text("", fontSize = 12.sp, color = SovereignBlue, fontWeight = FontWeight.Medium)
        }
    }
}

//@Composable
//fun EmptyAllocationState(label: String){ //test calling this 3 times and changing the label
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(120.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(text = label, color = Color.Gray, fontSize = 14.sp)
//    }
//}

@Composable
fun AllocationRowSection(allocations: List<Allocation>) {
    if (allocations.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No allocations set yet \n Tap View all to add", color = Color.Gray, fontSize = 14.sp)
        }
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            items(allocations) { allocation ->
                AllocationCard(
                    title = allocation.title,
                    amountLeft = "$${String.format("%.2f", allocation.amountLeft)} left",
                    totalBudget = "$${String.format("%.2f", allocation.totalBudget)}",
                    progress = allocation.progress,
                    icon = allocation.icon
                )
            }
        }
    }
}

//@Composable
//fun EmptyChartState(){
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(200.dp),
//            //.background(Color.White, RoundedCornerShape(16.dp)),
//            //.border(1.dp, CardOutline, RoundedCornerShape(16.dp)),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(text = "No trend data available", color = Color.Gray, fontSize = 14.sp)
//    }
//}

//@Composable
//fun EmptyLedgerState(){
//    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//        repeat(3){
//            Box(
//                modifier = Modifier
//                .fillMaxWidth()
//                .height(70.dp),
//                //.background(Color.White, RoundedCornerShape(16.dp)),
//                //.border(1.dp, CardOutline, RoundedCornerShape(16.dp)),
//                contentAlignment = Alignment.CenterStart
//            ) {
//                Text(
//                    text = "No recent transactions",
//                    color = Color.LightGray,
//                    fontSize = 13.sp,
//                    modifier = Modifier.padding(start = 16.dp)
//                )
//            }
//        }
//    }
//}

@Composable
fun RecentLedgerSection(transactions: List<Transaction>, onViewAll: () -> Unit) {
    if (transactions.isEmpty()) {
        repeat(3) {
            Box(
                modifier = Modifier.fillMaxWidth().height(70.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text("No recent transactions\n Tap View all or + to add", color = Color.LightGray,
                    fontSize = 13.sp, modifier = Modifier.padding(start = 16.dp))
            }
        }
    } else {
        Column {
            transactions.forEach { tx ->
                TransactionCard(
                    title = tx.title,
                    category = tx.category,
                    amount = "${if (tx.isExpense) "-" else "+"}$${String.format("%.2f", tx.amount)}",
                    time = tx.time,
                    date = tx.date,
                    icon = tx.icon,
                    isExpense = tx.isExpense
                )
                HorizontalDivider(color = BackgroundGray, thickness = 1.dp)
            }
        }
    }
}