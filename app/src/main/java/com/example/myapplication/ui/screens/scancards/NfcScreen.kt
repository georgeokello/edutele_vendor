package com.example.myapplication.ui.screens.scancards


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.ui.components.AppTemplate
import com.example.myapplication.ui.navigation.navItems
import com.example.myapplication.ui.util.navigateTo


@Composable
fun NfcScreen(navController: NavController) {


    val context = LocalContext.current
    val currentRoute = "nfc_Scan"
    val nfcData by NfcManager.nfcData.collectAsState()

    val userPreferences = remember {
        UserPreferences(context)
    }

    val viewModel: NfcViewModel = viewModel(
        factory = NfcViewModelFactory(userPreferences)
    )

    val username by viewModel.username.collectAsState()

    AppTemplate(
        userName = username.toString(),
        navItems = navItems,
        selectedNavIndex = navItems.indexOfFirst { it.route == currentRoute },
        onNavSelected = { index ->
            val route = navItems[index].route
            if (route != currentRoute) navigateTo(navController, route)
        }
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nfcData ?: "Tap NFC Tag...",
            )
        }
    }
}