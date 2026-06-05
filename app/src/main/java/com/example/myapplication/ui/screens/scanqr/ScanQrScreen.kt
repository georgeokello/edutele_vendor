package com.example.myapplication.ui.screens.scanqr

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import android.content.pm.PackageManager
import android.icu.text.CaseMap.Title
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.ui.components.AppTemplate
import com.example.myapplication.ui.navigation.navItems
import com.example.myapplication.ui.util.FailureDialog
import com.example.myapplication.ui.util.SuccessDialog
import com.example.myapplication.ui.util.navigateTo
import kotlinx.coroutines.delay



@Composable
fun ScanQRScreen(
    modifier: Modifier = Modifier, navController: NavController
) {

    val currentRoute = "scan_qr"

    val context = LocalContext.current
    val userPreferences = remember {
        UserPreferences(context)
    }


    val viewModel: ScanQrViewModel =
        viewModel(
            factory =
            ScanQrViewModelFactory(userPreferences)
        )

    val username by viewModel.username.collectAsState()


    val lifecycleOwner = LocalLifecycleOwner.current

    val cardHolder by viewModel.cardHolder.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val showEnterAmountDialog by viewModel.enterAmountDialog.collectAsState()
    val showPasswordDialog by viewModel.showPasswordDialog.collectAsState()
    val showConfirmationDialog by viewModel.showConfirmationDialog.collectAsState()
    val showErrorDialog by viewModel.showErrorDialog.collectAsState()



    var amount by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scannedQr = remember {
        mutableStateOf("")
    }

    val startScanning = remember {
        mutableStateOf(true)
    }

    var passwordVisible by remember { mutableStateOf(false) }



    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
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
            modifier = Modifier.fillMaxSize()
        ) {

            // CAMERA
            if (startScanning.value) {

                if (hasCameraPermission) {
                    AndroidView(

                        factory = { ctx ->

                            val previewView = PreviewView(ctx)

                            val cameraProviderFuture =
                                ProcessCameraProvider.getInstance(ctx)

                            cameraProviderFuture.addListener({

                                val cameraProvider =
                                    cameraProviderFuture.get()

                                val preview =
                                    Preview.Builder().build()

                                preview.setSurfaceProvider(
                                    previewView.surfaceProvider
                                )

                                val imageAnalysis =
                                    ImageAnalysis.Builder()
                                        .setBackpressureStrategy(
                                            ImageAnalysis
                                                .STRATEGY_KEEP_ONLY_LATEST
                                        )
                                        .build()

                                imageAnalysis.setAnalyzer(

                                    ContextCompat.getMainExecutor(ctx),

                                    QrCodeAnalyzer { qrText ->

                                        // IMPORTANT
                                        if (
                                            qrText.isNotEmpty() &&
                                            startScanning.value
                                        ) {

                                            scannedQr.value = qrText

                                            startScanning.value = false

                                            // function to check the token
                                            viewModel.checkToken(qrText)

                                        }
                                    }
                                )

                                try {

                                    cameraProvider.unbindAll()

                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                        preview,
                                        imageAnalysis
                                    )

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },

                        modifier = Modifier.fillMaxSize()
                    )
                }else{
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Camera permission is required to scan QR codes."
                        )
                    }
                }


            }

            // LOADING
            if (isLoading) {

                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
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
            if (showEnterAmountDialog) {


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
                                viewModel.closeEnterAmountDialog()

                                viewModel.processQrCode(viewModel.publicId.value, amount,remarks)
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

                                startScanning.value = true
                            },
                            shape = RoundedCornerShape(8.dp)
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
                                viewModel.confirmPay(password)
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

                                startScanning.value = true
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

            SuccessDialog(
                showDialog = showConfirmationDialog ,
                message = successMessage,
                onDismiss = {
                    viewModel.dismissConfirmDialog()
                    startScanning.value = true
                }
            )

            FailureDialog(
                showDialog =  showErrorDialog ,
                message = errorMessage,
                onDismiss = {
                    viewModel.dismissErrorDialog()
                    startScanning.value = true
                }
            )
        }
    }
}
