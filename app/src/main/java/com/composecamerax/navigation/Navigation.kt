package com.composecamerax.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.composecamerax.ui.screens.camera.ScreenCamera
import com.composecamerax.ui.screens.ScreenMain
import com.composecamerax.ui.screens.qrcode.ScreenQrCode

@androidx.camera.core.ExperimentalGetImage
@Composable
fun  Navigation(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = Screen.Main.route) {
        composable(route = Screen.Main.route) { backStackEntry ->
            backStackEntry.destination.label = Screen.Main::class.java.simpleName
            ScreenMain {
                navHostController.navigate(Screen.Camera.route)
            }
        }

        composable(route = Screen.Camera.route) { backStackEntry ->
            backStackEntry.destination.label = Screen.Camera::class.java.simpleName
            ScreenCamera {
                navHostController.navigate(Screen.QrCode.route)
            }
        }

        composable(route = Screen.QrCode.route) { backStackEntry ->
            backStackEntry.destination.label = Screen.QrCode::class.java.simpleName
            ScreenQrCode()
        }
    }
}