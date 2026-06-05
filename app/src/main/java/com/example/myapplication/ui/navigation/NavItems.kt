package com.example.myapplication.ui.navigation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myapplication.R


data class BottomNavItem(
    val label: String,
    @DrawableRes val icon: Int,
    val route: String
)

val navItems = listOf(
    BottomNavItem(
        "HOME",
        R.drawable.home_24px,
        "home"),
    BottomNavItem(
        "USE CARD",
        R.drawable.credit_card_24px,
        "nfc_Scan"),
    BottomNavItem(
        "SCAN QR",
        R.drawable.scan_24px,
        "scan_qr"),
    BottomNavItem(
        "HISTORY",
        R.drawable.receipt_long_24px,
        "history"),

)