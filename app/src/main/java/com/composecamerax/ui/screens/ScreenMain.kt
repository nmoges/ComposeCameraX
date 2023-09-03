package com.composecamerax.ui.screens

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composecamerax.ui.theme.ComposeCameraXTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScreenMain() {
    var isPermissionDeniedOnce by rememberSaveable { mutableStateOf(false) }
    var isPermissionDeniedTwice by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    if (cameraPermissionState.hasPermission.not()) {
        if (cameraPermissionState.shouldShowRationale) {
            isPermissionDeniedOnce = true
        } else {
            if (isPermissionDeniedOnce) {
                isPermissionDeniedTwice = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (cameraPermissionState.hasPermission) {
                    Log.d("CHECK_PERMISSION", "GO TO NEXT SCREEN")
                } else {
                    if (isPermissionDeniedTwice.not()) {
                        cameraPermissionState.launchPermissionRequest()
                    } else {
                        context.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        ) {
            Text(text = "Request permission")
        }
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        ) {
            Text(text = "Access Camera")
        }
    }
}

@Preview
@Composable
private fun PreviewMainScreen() {
    ComposeCameraXTheme {
        ScreenMain()
    }
}