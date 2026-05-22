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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.ui.components.AppTemplate
import com.example.myapplication.ui.navigation.navItems
import com.example.myapplication.ui.util.navigateTo


@Composable
fun ScanQRScreen(modifier: Modifier = Modifier, navController: NavController) {

    val currentRoute = "scan_qr"

    val context = LocalContext.current
    val userPreferences = remember {
        UserPreferences(context)
    }

    val lifecycleOwner =
        LocalLifecycleOwner.current

    val viewModel: ScanQrViewModel =
        viewModel(
            factory =
            ScanQrViewModelFactory(userPreferences)
        )

    val username by viewModel.username.collectAsState()

    var amount by remember {
        mutableStateOf("")
    }

    var remarks by remember {
        mutableStateOf("")
    }

    var startScanning by remember {
        mutableStateOf(false)
    }

    var qrDetected by remember {
        mutableStateOf(false)
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                startScanning = true
            }
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
       // content
        if (startScanning) {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                AndroidView(

                    factory = { ctx ->

                        val previewView =
                            PreviewView(ctx)

                        val cameraProviderFuture =
                            ProcessCameraProvider
                                .getInstance(ctx)

                        cameraProviderFuture
                            .addListener({

                                val cameraProvider =
                                    cameraProviderFuture.get()

                                val preview =
                                    Preview.Builder()
                                        .build()

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

                                    ContextCompat
                                        .getMainExecutor(ctx),

                                    QrCodeAnalyzer { qrText ->

                                            viewModel.processQrCode(
                                                qrText, amount, remarks
                                            )

                                    }
                                )

                                try {

                                    cameraProvider.unbindAll()

                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector
                                            .DEFAULT_BACK_CAMERA,
                                        preview,
                                        imageAnalysis
                                    )

                                } catch (e: Exception) {

                                    e.printStackTrace()
                                }

                            },

                                ContextCompat
                                    .getMainExecutor(ctx)
                            )

                        previewView
                    },

                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier =
                    Modifier.fillMaxWidth()
                ) {

                    if (viewModel.isLoading.value) {

                        LinearProgressIndicator(
                            modifier =
                            Modifier.fillMaxWidth()
                        )
                    }

                    if(viewModel.resultMessage.value.isNotEmpty()){
                        Text(text = viewModel.resultMessage.value)
                    }

                    if (
                        viewModel.errorMessage.value
                            .isNotEmpty()
                    ) {

                        Text(
                            text =
                            viewModel.errorMessage.value
                        )
                    }

                    var password by remember {
                        mutableStateOf("")
                    }

                    if (viewModel.showPasswordDialog.value) {

                        AlertDialog(

                            onDismissRequest = { },

                            title = {
                                Text("Confirm Password")
                            },

                            text = {

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = {
                                        password = it
                                    },
                                    label = {
                                        Text("Password")
                                    }
                                )
                            },

                            confirmButton = {

                                Button(
                                    onClick = {
                                        // Handle password confirmation
                                        viewModel.confirmPay(password)
                                    }
                                ) {

                                    Text("Confirm")
                                }
                            },
                            dismissButton = {

                                OutlinedButton(
                                    onClick = {

                                        viewModel.dismissPasswordDialog()
                                    }
                                ) {

                                    Text("Cancel")
                                }
                            }
                        )
                    }
                    if(viewModel.showConfirmationDialog.value){
                        AlertDialog(
                            onDismissRequest = { /*TODO*/ },
                            title = {
                                Text(text = "Payment Confirmed")
                            },
                            text = {
                                Text(
                                    text =
                                    "New balance is now ${
                                        viewModel.qrResponse.value
                                            ?.balance_after ?: ""
                                    }"
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        // close dialog
                                        viewModel.dismissConfirmDialog()
                                    }
                                ) {

                                    Text("Close")
                                }
                            }

                        )
                    }
                }
            }

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),

                verticalArrangement =
                Arrangement.Center
            ) {

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                    },
                    label = {
                        Text("Enter Amount")
                    },
                    modifier =
                    Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                OutlinedTextField(
                    value = remarks,
                    onValueChange = {
                        remarks = it
                    },
                    label = {
                        Text("Remarks")
                    },
                    modifier =
                    Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Button(
                    onClick = {

                        permissionLauncher.launch(
                           Manifest.permission.CAMERA
                        )
                       // viewModel.processQrCode(
                         //  qrData = "4rYhJBg3hGYt8C154vSTRg28GlP1yvxVlkXobkGZwoA",
                        //    amount = amount,
                         //   remarks = remarks
                        //)
                    },
                    modifier =
                    Modifier.fillMaxWidth()
                ) {

                    Text("Scan QR")
                }
            }
        }
    }


}
