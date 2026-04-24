package com.example.sovereignledger.ui.screens

//Ledger or transaction
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sovereignledger.data.model.Allocation
import com.example.sovereignledger.data.model.Transaction
import com.example.sovereignledger.ui.components.AppButton
import com.example.sovereignledger.ui.components.EmptyStateContent
import com.example.sovereignledger.ui.components.KeyPad
import com.example.sovereignledger.ui.components.TransactionCard
import com.example.sovereignledger.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionScreen(
    transactions: List<Transaction>,
    allocations: List<Allocation>,
    onAddTransaction: (Transaction) -> Unit,
    initialTab: String = "manual"
){

    var showForm by remember { mutableStateOf(false) }
    var formTab by remember { mutableStateOf(initialTab) }
    var nextId by remember { mutableIntStateOf(transactions.size + 1) }

    LaunchedEffect(initialTab) { formTab = initialTab }
    Box(modifier = Modifier.fillMaxSize().background(BackgroundGray)) {

        if (transactions.isEmpty() && !showForm) {
            EmptyStateContent(
                icon = Icons.Outlined.Receipt,
                title = "No transactions yet",
                subtitle = "Tap Quick Add to record your first transaction",
                onQuickAdd = { showForm = true; formTab = "manual" }
            )
        } else if (!showForm) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
            ) {
                items(transactions) { tx ->
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
                item {
                    EmptyStateContent(
                       icon = Icons.Outlined.Receipt,
                        title = "No transactions yet",
                        subtitle = "Tap Quick Add to record your first transaction",
                        onQuickAdd = { showForm = true; formTab = "manual" }
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = showForm,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.fillMaxSize()
        ) {
            NewTransactionForm(
                allocations = allocations,
                initialTab = formTab,
                onSave = { tx ->
                    onAddTransaction(tx.copy(id = nextId++))
                    showForm = false
                },
                onDismiss = { showForm = false }
            )
        }
    }
}

@Composable
fun NewTransactionForm(
    allocations: List<Allocation>,
    initialTab: String = "manual",
    onSave: (Transaction) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundGray)) {

         Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceWhite)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Manual" to "manual", "Capture" to "capture", "Upload Data" to "upload")
                .forEach { (label, key) ->
                    val isSelected = selectedTab == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) SovereignBlue.copy(alpha = 0.12f) else Color.Transparent)
                            .clickable { selectedTab = key }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            label, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                            color = if (isSelected) SovereignBlue else TextSecondary
                        )
                    }
                }
        }

        when (selectedTab) {
            "manual" -> ManualTransactionTab(
                allocations = allocations,
                onSave = onSave,
                onDismiss = onDismiss
            )
            "capture" -> PlaceholderTab(
                icon = Icons.Default.DocumentScanner,
                message = "Capture something",
                onSave = onDismiss
            )
            "upload" -> PlaceholderTab(
                icon = Icons.Default.UploadFile,
                message = "Capture something",
                onSave = onDismiss
            )
        }
    }
}
@Composable
fun ManualTransactionTab(
    allocations: List<Allocation>,
    onSave: (Transaction) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedAllocation by remember { mutableStateOf<Allocation?>(null) }
    var isExpense by remember { mutableStateOf(true) }
    var amountDisplay by remember { mutableStateOf("0.00") }
    var rawAmount by remember { mutableStateOf("") }
    var showKeypad by remember { mutableStateOf(false) }
    var allocationExpanded by remember { mutableStateOf(false) }

    val now = remember {
        val sdf = SimpleDateFormat("hh:mma", Locale.getDefault())
        val sdf2 = SimpleDateFormat("MMM dd", Locale.getDefault())
        Pair(sdf.format(Date()).lowercase(), sdf2.format(Date()))
    }

    fun handleNumber(key: String) {
        if (key == "." && rawAmount.contains(".")) return
        if (rawAmount.length >= 10) return
        rawAmount += key
        amountDisplay = String.format("%.2f", rawAmount.toDoubleOrNull() ?: 0.0)
    }

    fun handleDelete() {
        if (rawAmount.isNotEmpty()) {
            rawAmount = rawAmount.dropLast(1)
            amountDisplay = String.format("%.2f", rawAmount.toDoubleOrNull() ?: 0.0)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceWhite)
                .padding(20.dp)
                .clickable { showKeypad = true },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text("$", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                    color = SovereignNavy, modifier = Modifier.padding(top = 8.dp))
                Text(amountDisplay, fontSize = 52.sp, fontWeight = FontWeight.ExtraBold, color = SovereignNavy)
            }
        }

        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))


            FormLabel("Add Transaction")
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(SurfaceWhite).padding(14.dp)
            ) {
                BasicTextField(
                    value = title, onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (title.isEmpty()) Text("John Doe", color = Color.LightGray, fontSize = 14.sp)
                        inner()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            FormLabel("Type")
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(true to "Expense", false to "Income").forEach { (type, label) ->
                    val isSelected = isExpense == type
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) SovereignBlue else SurfaceWhite)
                            .clickable { isExpense = type }.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FormLabel("Add Allocation")
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceWhite)
                        .clickable { allocationExpanded = true }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedAllocation?.title ?: "Select allocation (optional)",
                        fontSize = 14.sp,
                        color = if (selectedAllocation != null) SovereignNavy else Color.LightGray
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
                }

                DropdownMenu(
                    expanded = allocationExpanded,
                    onDismissRequest = { allocationExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = { Text("None", color = TextSecondary) },
                        onClick = { selectedAllocation = null; allocationExpanded = false }
                    )
                    allocations.forEach { alloc ->
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(alloc.icon, contentDescription = null,
                                    tint = SovereignNavy, modifier = Modifier.size(18.dp))
                            },
                            text = { Text(alloc.title, color = SovereignNavy) },
                            onClick = { selectedAllocation = alloc; allocationExpanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FormLabel("Notes")
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(SurfaceWhite).padding(14.dp)
            ) {
                BasicTextField(
                    value = notes, onValueChange = { notes = it },
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
                    if (amount > 0.0 && title.isNotBlank()) {
                        onSave(
                            Transaction(
                                id = 0,
                                title = title,
                                category = selectedAllocation?.title ?: "Other",
                                amount = amount,
                                time = now.first,
                                date = now.second,
                                icon = selectedAllocation?.icon ?: Icons.Default.Receipt,
                                isExpense = isExpense,
                                allocationId = selectedAllocation?.id,
                                notes = notes
                            )
                        )
                    }
                }
            )
        }

        AnimatedVisibility(
            visible = showKeypad,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().background(BackgroundGray).padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showKeypad = false }) {
                        Text("Done", color = SovereignBlue, fontWeight = FontWeight.Bold)
                    }
                }
                KeyPad(onNumberClick = { handleNumber(it) }, onDeleteClick = { handleDelete() })
            }
        }
    }
}

@Composable
fun PlaceholderTab(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String, onSave: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Icon(icon, contentDescription = null, tint = SovereignBlue, modifier = Modifier.size(64.dp))
        Text(message, fontSize = 16.sp, color = TextSecondary, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.weight(1f))
        AppButton(text = "Save Up!", onClick = onSave)
        Spacer(modifier = Modifier.height(16.dp))
    }
}