package com.composecamerax

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.composecamerax.navigation.Navigation
import com.composecamerax.ui.theme.ComposeCameraXTheme

class MainActivity : ComponentActivity() {

    private lateinit var navHostController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCameraXTheme {
                navHostController = rememberNavController()
                Navigation(navHostController = this.navHostController)
            }
        }
    }
}
