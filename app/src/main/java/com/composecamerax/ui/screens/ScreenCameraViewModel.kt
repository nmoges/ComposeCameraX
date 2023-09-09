package com.composecamerax.ui.screens

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScreenCameraViewModel: ViewModel() {

    private val _bitmapQrCode: MutableStateFlow<ImageBitmap?> = MutableStateFlow(null)
    val bitmapQrCode: StateFlow<ImageBitmap?>
        get() = _bitmapQrCode

    fun generateBitmap(rawData: String) {
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(rawData, BarcodeFormat.QR_CODE, 200, 200)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.createBitmap(bitMatrix)
        _bitmapQrCode.value = bitmap.asImageBitmap()
    }
}