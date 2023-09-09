package com.composecamerax.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
@Composable
fun ScreenCamera() {
    // TODO : to implement
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        CameraView()
    }
}

@SuppressLint("ClickableViewAccessibility")
@androidx.camera.core.ExperimentalGetImage
@Composable
private fun CameraView() {
    val lifecycleOwner = LocalLifecycleOwner.current
    lateinit var camera: Camera

    Column {
        AndroidView(factory = { context ->

            // Initialize Camera preview
            val previewView = getPreviewView(context)
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            // Initialize Camera
            camera = getCameraProvider(context).bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                getAnalysisUserCase()
            )

            // Set Camera interactions
            val gestureListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val zoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio
                    zoomRatio?.let {
                        val scale = it * detector.scaleFactor
                        camera.cameraControl.setZoomRatio(scale)
                    }
                    return true
                }
            }
            val scaleGestureDetector = ScaleGestureDetector(context, gestureListener)

            previewView.setOnTouchListener { view, motionEvent ->
                scaleGestureDetector.onTouchEvent(motionEvent)
                return@setOnTouchListener true
            }

            previewView
        }
        )
    }
}

private fun getPreviewView(context: Context) = PreviewView(context).apply {
    this.scaleType = PreviewView.ScaleType.FILL_CENTER
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
}

private fun getCameraProvider(context: Context) =
    ProcessCameraProvider.getInstance(context).get().also {
        it.unbindAll()
    }


private fun getBarcodeScanner(): BarcodeScanner {
    val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
        Barcode.FORMAT_QR_CODE
    ).build()
    return BarcodeScanning.getClient(options)
}

@androidx.camera.core.ExperimentalGetImage
private fun getAnalysisUserCase(): ImageAnalysis {
    val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
        Barcode.FORMAT_QR_CODE
    ).build()

    val scanner = BarcodeScanning.getClient(options)

    val analysisUseCase = ImageAnalysis.Builder().build().also {
        it.setAnalyzer(
            Executors.newSingleThreadExecutor()
        ) { imageProxy ->
            processImageProxy(scanner, imageProxy)
        }
    }
    return analysisUseCase
}

@androidx.camera.core.ExperimentalGetImage
private fun processImageProxy(
    scanner: BarcodeScanner,
    imageProxy: ImageProxy
) {
    imageProxy.image?.let {
        val inputImage = InputImage.fromMediaImage(
            it,
            imageProxy.imageInfo.rotationDegrees
        )

        scanner.process(inputImage)
            .addOnSuccessListener { barcodeList ->
                barcodeList.forEach {
                    Log.d("BARCODE", "Value : ${it.rawValue}")
                }
            }.addOnFailureListener { exception ->
                Log.d("BARCODE", "Error : ${exception.localizedMessage}")
                exception.printStackTrace()
            }.addOnCompleteListener {
                imageProxy.image?.close()
                imageProxy.close()
            }
    }
}