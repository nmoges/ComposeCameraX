package com.composecamerax.ui.screens.camera

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScreenCameraViewModel: ViewModel() {

    private val _barcode: MutableStateFlow<Barcode?> = MutableStateFlow(null)
    val barcode: StateFlow<Barcode?>
        get() = _barcode

    fun updateBarcode(barcode: Barcode?) {
        _barcode.value = barcode
    }

    fun generateBitmap(): ImageBitmap {
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(_barcode.value?.rawValue, BarcodeFormat.QR_CODE, 200, 200)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.createBitmap(bitMatrix)
        return bitmap.asImageBitmap()
    }
}