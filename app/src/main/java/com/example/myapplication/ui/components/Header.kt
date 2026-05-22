package com.example.myapplication.ui.components


import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.ui.theme.MainBlue
import com.example.myapplication.ui.theme.MainWhite

import androidx.compose.material3.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    icon: ImageVector,
    onIconClick: () -> Unit,
    containerColor: Color = MainBlue,
    contentColor: Color = MainWhite
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "Navigation icon",
                modifier = Modifier.clickable { onIconClick() }
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor
        )
    )
}

