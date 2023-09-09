package com.composecamerax.navigation

sealed class Screen(val route: String) {
    object Main : Screen("screen_main")
    object Camera : Screen("screen_camera")
    object QrCode : Screen("screen_qrcode")
}