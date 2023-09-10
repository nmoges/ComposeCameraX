package com.composecamerax.ui.screens.camera

import android.annotation.SuppressLint
import android.content.Context
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
@Composable
fun ScreenCamera(viewModel: ScreenCameraViewModel = koinViewModel(), onClick: () -> Unit) {
    val barcode = viewModel.barcode.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CameraView(viewModel)
        barcode.value?.let { barcode ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    bitmap = viewModel.generateBitmap(),
                    contentDescription = "",
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.padding(top = 15.dp))
                Text(
                    text = "Data : ${barcode.rawValue}",
                    color = Color.White
                )
                Spacer(modifier = Modifier.padding(top = 15.dp))
                Row {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
                                viewModel.updateBarcode(null)
                            }
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
                                viewModel.updateBarcode(null)
                                onClick.invoke()
                            }
                    )
                }
            }
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
@androidx.camera.core.ExperimentalGetImage
@Composable
private fun CameraView(viewModel: ScreenCameraViewModel) {
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
                getAnalysisUserCase(viewModel)
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

@androidx.camera.core.ExperimentalGetImage
private fun getAnalysisUserCase(viewModel: ScreenCameraViewModel): ImageAnalysis {
    val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
        Barcode.FORMAT_QR_CODE
    ).build()

    val scanner = BarcodeScanning.getClient(options)

    val analysisUseCase = ImageAnalysis.Builder().build().also {
        it.setAnalyzer(
            Executors.newSingleThreadExecutor()
        ) { imageProxy ->
            processImageProxy(scanner, imageProxy, viewModel)
        }
    }

    return analysisUseCase
}

@androidx.camera.core.ExperimentalGetImage
private fun processImageProxy(
    scanner: BarcodeScanner,
    imageProxy: ImageProxy,
    viewModel: ScreenCameraViewModel
) {
    imageProxy.image?.let {
        val inputImage = InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)
        scanner.process(inputImage)
            .addOnSuccessListener { barcodeList ->
                if (barcodeList.isNotEmpty()) {
                    val barcode = barcodeList[0]
//                    barcode.rawValue?.let { rawData -> viewModel.updateBarcode(barcode) }
                    viewModel.updateBarcode(barcode)
                }
            }.addOnFailureListener { exception ->
                exception.printStackTrace()
            }.addOnCompleteListener {
                imageProxy.image?.close()
                imageProxy.close()
            }
    }
}