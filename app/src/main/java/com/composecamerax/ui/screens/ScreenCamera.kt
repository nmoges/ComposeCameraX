package com.composecamerax.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
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
                preview
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