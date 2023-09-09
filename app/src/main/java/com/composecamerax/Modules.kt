package com.composecamerax

import com.composecamerax.ui.screens.camera.ScreenCameraViewModel
import com.composecamerax.ui.screens.qrcode.ScreenQrCodeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ScreenCameraViewModel() }
    viewModel { ScreenQrCodeViewModel() }
}

val listModules: List<Module> = listOf(
    viewModelModule
)