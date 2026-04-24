package com.example.sovereignledger.navigation


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sovereignledger.data.model.Allocation
import com.example.sovereignledger.data.model.Transaction
import com.example.sovereignledger.ui.components.BottomNavBar
import com.example.sovereignledger.ui.components.MyAppTopBar
import com.example.sovereignledger.ui.components.QuickAcionFAB
import com.example.sovereignledger.ui.screens.AllocationListScreen
import com.example.sovereignledger.ui.screens.BudgetScreen
import com.example.sovereignledger.ui.screens.CategoryOption
import com.example.sovereignledger.ui.screens.IDVerScreen
import com.example.sovereignledger.ui.screens.NewTransactionForm
import com.example.sovereignledger.ui.screens.OverviewScreen
import com.example.sovereignledger.ui.screens.TransactionScreen
import com.example.sovereignledger.ui.screens.allCategories

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var isFabExpanded by remember { mutableStateOf(false) }
    var categories by remember { mutableStateOf(allCategories.toMutableList() as List<CategoryOption>) }
    var allocations by remember { mutableStateOf<List<Allocation>>(emptyList()) }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    val isTopLevel = currentRoute in listOf("overview", "budgets", "insights", "settings")


    //update allocation if transaction happens
    fun addTransaction(tx: Transaction) {
        transactions = transactions + tx
        if (tx.allocationId != null && tx.isExpense) {
            allocations = allocations.map { alloc ->
                if (alloc.id == tx.allocationId)
                    alloc.copy(amountSpent = alloc.amountSpent + tx.amount)
                else alloc
            }
        }
    }
    if(currentRoute == "verification" || currentRoute == null){
        NavHost(
            navController = navController,
            startDestination = "verification") {

            composable ("verification"){
                IDVerScreen(
                    onVerificationSuccess = {
                        navController.navigate("overview") {
                            popUpTo("verification") {
                                inclusive = true
                            } //the user cant go back to verification from there, removes it from backstack
                        }
                    },
                    onCancel = {}
                )
            }
            composable ("overview"){  }
        }
    } else {
        Scaffold(modifier = Modifier.padding(top = 24.dp),
            topBar = { MyAppTopBar(
                title = when (currentRoute) {
                    "allocations" -> "Allocation"
                    "ledger" -> "Ledger"
                    "add_transaction_manual" -> "Add Transaction"
                    "add_transaction_capture" -> "Add Transaction"
                    "add_transaction_upload" -> "Add Transaction"
                    "budgets" -> "Budgets & Limits"
                    else -> ""
                },
                isDashboard = isTopLevel,
                onBackClick = { navController.popBackStack() }
            ) },
            bottomBar = {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo( //switch tabs without stacking screens
                                navController.graph.startDestinationId  //go back to root of the app before navigating
                            ) { saveState = true } //save state of previous stuff
                            launchSingleTop = true //open only once
                            restoreState = true //restore saved state
                        }
                    }
                )
            },
            floatingActionButton = {
                if(currentRoute == "overview") {
                    QuickAcionFAB(
                        expanded = isFabExpanded,
                        onToggle = { isFabExpanded = !isFabExpanded },
                        onManualClick = {
                            isFabExpanded = false
                            navController.navigate("add_transaction_manual")
                        },
                        onCaptureClick = {
                            isFabExpanded = false
                            navController.navigate("add_transaction_capture")
                        },
                        onUploadClick = {
                            isFabExpanded = false
                            navController.navigate("add_transaction_upload")
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = "overview"
                ) {
                    composable("overview") {
                        OverviewScreen(
                            isDimmed = isFabExpanded,
                            allocations = allocations,
                            transactions = transactions,
                            onViewAllAllocations = { navController.navigate("allocations") },
                            onViewAllLedger = { navController.navigate("ledger") }  )
                    }
                    composable("allocations") {
                        AllocationListScreen(
                            allocations = allocations,
                            onAddAllocation = { allocations = allocations + it }
                        )
                    }
                    composable("ledger") {
                        TransactionScreen(
                            transactions = transactions,
                            allocations = allocations,
                            onAddTransaction = { addTransaction(it) }
                        )
                    }
                    composable("add_transaction_manual") {
                        NewTransactionForm(
                            allocations = allocations,
                            initialTab = "manual",
                            onSave = { addTransaction(it); navController.popBackStack() },
                            onDismiss = { navController.popBackStack() }
                        )
                    }
                    composable("add_transaction_capture") {
                        NewTransactionForm(
                            allocations = allocations,
                            initialTab = "capture",
                            onSave = { addTransaction(it); navController.popBackStack() },
                            onDismiss = { navController.popBackStack() }
                        )
                    }
                    composable("add_transaction_upload") {
                        NewTransactionForm(
                            allocations = allocations,
                            initialTab = "upload",
                            onSave = { addTransaction(it); navController.popBackStack() },
                            onDismiss = { navController.popBackStack() }
                        )
                    }
                    composable("budgets") {
                        BudgetScreen(
                            allocations = allocations,
                            transactions = transactions,
                            categories = categories,
                            onAddCategory = { newCat ->
                                categories = categories + newCat
                            })
                    }
                    composable("insights") { }
                    composable("settings") { }
                }
            }
        }
    }
}