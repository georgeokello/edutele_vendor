package com.example.myapplication.ui.screens.homescreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.ui.components.AppTemplate
import com.example.myapplication.ui.navigation.navItems
import com.example.myapplication.ui.util.navigateTo
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import com.example.myapplication.data.model.home.AccessAllocation
import com.example.myapplication.data.model.home.MainStats
import com.example.myapplication.data.model.home.QuickStats
import com.example.myapplication.data.model.home.RecentRedemptions



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(userPreferences)
    )

    val user by viewModel.username.collectAsState()

    val currentRoute = "home"

    val dashboard by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCardInfo()
    }

    AppTemplate(
        userName=user.toString(),
        navItems = navItems,
        selectedNavIndex = navItems.indexOfFirst { it.route == currentRoute },
        navController = navController,
        onNavSelected = { index ->
            val route = navItems[index].route
            if (route != currentRoute) navigateTo(navController, route)
        }

    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            Column() {
                Text(
                    text = "Access Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                dashboard.mainStats?.let {
                    dashboard.accessAllocation?.let { it1 ->
                        AccessOverviewCard(
                            mainStats = it,
                            accessAllocation = it1
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Recent Redemptions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),

                ) {

                    val activities = dashboard.recentRedemptions

                    items(
                        items = activities,
                            key = { it.reference }
                        ) { activity ->
                            RecentHistory(activity)
                    }

                }
            }
        }
    }
}

@Composable
fun RecentHistory(recentRedemptions: RecentRedemptions) {

    val statusColor = when (recentRedemptions.status.lowercase()) {
        "success", "completed", "paid" -> Color(0xFF16A34A)
        "pending" -> Color(0xFFF59E0B)
        "failed", "declined" -> Color(0xFF990000)
        else -> Color(0xFF6B7280)
    }

    val amountColor = if (recentRedemptions.amount.trim().startsWith("-")) {
        Color(0xFF990000) // red
    } else {
        Color(0xFF16A34A) // green (or your default)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3E4E6)),
    ) {

        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {

            // TOP ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = recentRedemptions.activity ?: "",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )

                Text(
                    text = recentRedemptions.amount ?: "",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = amountColor
                )
            }

            // "HR" DIVIDER
            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFFCFCFCF)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // BOTTOM ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = recentRedemptions.card_holder ?: "",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )

                    Text(
                        text = recentRedemptions.date ?: "",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }

                Text(
                    text = recentRedemptions.status.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }
        }
    }
}


@Composable
fun AccessOverviewCard(
    mainStats: MainStats,
    accessAllocation: AccessAllocation
) {


    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = "Available Access",
                color = Color.Gray,
                fontSize = 13.sp
            )

            Text(
                text = "UGX ${"%,.0f".format(accessAllocation.available_balance) ?: ""}",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement =
                Arrangement.spacedBy(12.dp)
            ) {

                MiniStatChip(
                    modifier = Modifier.weight(1f),
                    title = "Load",
                    value = accessAllocation.load_count.toString() ?: ""
                )

                MiniStatChip(
                    modifier = Modifier.weight(1f),
                    title = "Access Used",
                    value = accessAllocation.sales_processed.toString() ?: ""
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

        }
    }
}

@Composable
fun MiniStatChip(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier,
        color = Color(0xFFF8F9FB),
        shape = RoundedCornerShape(8.dp)
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Text(
                text = title,
                color = Color.Gray,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
