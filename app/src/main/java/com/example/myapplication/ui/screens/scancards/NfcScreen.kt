package com.example.myapplication.ui.screens.scancards


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.ui.components.AppTemplate
import com.example.myapplication.ui.navigation.navItems
import com.example.myapplication.ui.util.FailureDialog
import com.example.myapplication.ui.util.SuccessDialog
import com.example.myapplication.ui.util.debug.NfcDebug
import com.example.myapplication.ui.util.getGreeting
import com.example.myapplication.ui.util.navigateTo


@Composable
fun NfcScreen(navController: NavController) {


    val context = LocalContext.current
    val currentRoute = "nfc_Scan"
    val nfcData by NfcManager.nfcData.collectAsState()
    val hasScanned by NfcManager.hasScanned.collectAsState()

    //val nfcData =  "04:B5:89:6A:B2:22:90"

    //NfcManager.True()


    val userPreferences = remember {
        UserPreferences(context)
    }

    val viewModel: NfcViewModel = viewModel(
        factory = NfcViewModelFactory(userPreferences)
    )

    val username by viewModel.username.collectAsState()

    var amount by remember {
        mutableStateOf("")
    }

    var remarks by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember { mutableStateOf(false) }

    val showAmountDialog by viewModel.enterAmountDialog.collectAsState()
    val showPasswordDialog by viewModel.showPasswordDialog.collectAsState()
    val showConfirmationDialog by viewModel.showConfirmationDialog.collectAsState()
    val showErrorDialog by viewModel.showErrorDialog.collectAsState()

    val cardHolder by viewModel.cardHolder.collectAsState()
    val publicId by viewModel.publicId.collectAsState()

    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    // Runs only when hasScanned changes
    LaunchedEffect(hasScanned) {
        if (hasScanned) {
            nfcData?.let { viewModel.checkNfcUid(it) }
        }
    }

    val activity = LocalContext.current as MainActivity

    // ✅ TURN NFC ON when screen opens
    LaunchedEffect(Unit) {
        activity.enableNfc()
    }

    // ✅ TURN NFC OFF when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            activity.disableNfc()
        }
    }

    AppTemplate(
        userName = username ?: "",
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    painter = painterResource(R.drawable.contactless_24px),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (hasScanned)
                        "Processing card..."
                    else
                        "Tap card to scan"
                )

                Spacer(modifier = Modifier.height(8.dp))

            }

            // ERROR
            if (errorMessage.isNotEmpty()) {

                LaunchedEffect(errorMessage) {

                    if (
                        errorMessage ==
                        "Invalid or expired token."
                    ) {

                        navController.navigate("login") {

                            popUpTo(0)
                            launchSingleTop = true
                        }
                    }
                }

            }

            // ENTER AMOUNT DIALOG
            if (showAmountDialog) {

                AlertDialog(
                    onDismissRequest = {
                        viewModel.closeEnterAmountDialog()
                    },
                    shape = RoundedCornerShape(8.dp),
                    containerColor = Color.White,
                    tonalElevation = 8.dp,

                    title = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {

                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE8F5E9)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.sync_alt_24px),
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(32.dp)
                                )
                            }

                            Text(
                                text = "Requesting Transfer From",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = cardHolder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    },

                    text = {
                        Column(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {

                            OutlinedTextField(
                                value = amount,
                                onValueChange = {
                                    if (it.matches(Regex("^\\d*$"))) {
                                        amount = it
                                    }
                                },
                                label = { Text("Amount") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.money_range_24px),
                                        contentDescription = null,
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = remarks,
                                onValueChange = { remarks = it },
                                label = { Text("Remarks") },
                                singleLine = false,
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.text_snippet_24px),
                                        contentDescription = null,
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },

                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.processQrCode(publicId, amount, remarks)
                            },
                            enabled = amount.isNotBlank(),
                            modifier = Modifier.padding(end = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                disabledContainerColor = Color(0xFFBDBDBD)
                            )
                        ) {
                            Text("Proceed")
                        }
                    },

                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                viewModel.closeEnterAmountDialog()
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // PASSWORD DIALOG
            if (showPasswordDialog) {

                AlertDialog(

                    onDismissRequest = {},

                    shape = RoundedCornerShape(8.dp),

                    containerColor = Color.White,

                    icon = {

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE8F5E9)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.payments_24px),
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(32.dp)
                            )
                        }
                    },

                    title = {

                        Text(
                            text = "Confirm Transfer",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },

                    text = {

                        Column {

                            Text(
                                text = "You are about to transfer",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = amount,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2E7D32),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    if (it.all(Char::isDigit) && it.length <= 6) {
                                        password = it
                                    }
                                },
                                label = {
                                    Text("Enter Pin")
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.NumberPassword
                                ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                visualTransformation =
                                if (passwordVisible)
                                    VisualTransformation.None
                                else
                                    PasswordVisualTransformation(),

                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            passwordVisible = !passwordVisible
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                id = if (passwordVisible)
                                                    R.drawable.visibility_off_24px
                                                else
                                                    R.drawable.visibility_24px
                                            ),
                                            contentDescription = null,
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            )
                        }
                    },

                    confirmButton = {
                        val isPasswordValid = password.length >= 5
                        Button(
                            onClick = {
                                viewModel.confirmPay(publicId, password)
                            },
                            enabled = isPasswordValid,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                disabledContainerColor = Color(0xFFBDBDBD)
                            )
                        ) {
                            Text(
                                text = "Confirm Transfer",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },

                    dismissButton = {

                        OutlinedButton(
                            onClick = {
                                viewModel.dismissPasswordDialog()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // CONFIRMATION DIALOG
            SuccessDialog(
                showDialog = showConfirmationDialog,
                message = successMessage,
                onDismiss = { viewModel.dismissConfirmDialog() }
            )

            FailureDialog(
                showDialog = showErrorDialog,
                message = errorMessage ,
                onDismiss = {
                    viewModel.closeErrorDialog()
                }
            )

        }
    }
}