package com.example.myapplication.ui.screens.transaction

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.model.transactions.TransactionItem
import com.example.myapplication.ui.components.AppTemplate
import com.example.myapplication.ui.navigation.navItems
import com.example.myapplication.ui.util.navigateTo

@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun TransactionScreen(navController: NavController) {

    val context = LocalContext.current
    val currentRoute = "history"

    val userPreferences = UserPreferences(context)

    val viewModelTransaction: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(userPreferences)
    )

    val state by viewModelTransaction.uiState.collectAsState()

    val username by viewModelTransaction.username.collectAsState()


    LaunchedEffect(Unit) {
        viewModelTransaction.fetchTransactions()
    }

    AppTemplate(
        userName = username.toString(),
        navItems = navItems,
        selectedNavIndex = navItems.indexOfFirst { it.route == currentRoute },
        onNavSelected = { index ->
            val route = navItems[index].route
            if (route != currentRoute) navigateTo(navController, route)
        }
    ) {
        Column {
            Text(text = "Transaction History")
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    when {
                        state.isLoading -> {
                            CircularProgressIndicator()
                        }

                        state.error != null -> {
                            Text(
                                text = state.error ?: "Unknown error",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        else -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.transactions) { transaction ->
                                    TransactionCard(transaction, viewModelTransaction)
                                }
                            }
                        }
                    }
                }
            }
        }
       
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionCard(transaction: TransactionItem, viewModel: TransactionViewModel) {

    val backgroundColor = when (transaction.status) {
        "posted" -> Color(0xFF81C784)
        "pending" -> Color(0xFFFFF3CD)
        "failed" -> Color(0xFFF8D7DA)
        else -> Color.LightGray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Status: ${transaction.status}")
            Text(text = "Type: ${transaction.type}")
            Text(text = "Amount: ${transaction.amount}")
            Text(text = "New Balance: ${transaction.balance_after}")
            Text(text = "Date: ${viewModel.formatTimestamp(transaction.timestamp)}")
        }
    }
}