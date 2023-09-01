package com.composecamerax

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.composecamerax.ui.screens.ScreenMain
import com.composecamerax.ui.theme.ComposeCameraXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCameraXTheme {
                ScreenMain()
            }
        }
    }
}
