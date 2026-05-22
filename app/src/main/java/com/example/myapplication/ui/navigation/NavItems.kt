package com.example.myapplication.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val navItems = listOf(
    BottomNavItem("USE CARD", Icons.Default.DateRange, "nfc_Scan"),
    BottomNavItem("SCAN QR", Icons.Default.Settings, "scan_qr"),
    BottomNavItem("HISTORY", Icons.Default.DateRange, "history")

)