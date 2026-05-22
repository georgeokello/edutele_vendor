package com.example.myapplication.ui.screens.scanqr

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer(
    private val onQrDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner =
        BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(
        imageProxy: ImageProxy
    ) {

        val mediaImage = imageProxy.image

        if (mediaImage != null) {

            val image =
                InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )

            scanner.process(image)

                .addOnSuccessListener { barcodes ->

                    for (barcode in barcodes) {

                        val value =
                            barcode.rawValue

                        if (!value.isNullOrEmpty()) {

                            onQrDetected(value)

                            break
                        }
                    }
                }

                .addOnFailureListener {

                    it.printStackTrace()
                }

                .addOnCompleteListener {

                    imageProxy.close()
                }

        } else {

            imageProxy.close()
        }
    }
}