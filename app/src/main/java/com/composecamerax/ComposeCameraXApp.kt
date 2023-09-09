package com.composecamerax

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ComposeCameraXApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ComposeCameraXApp)
            modules(listModules)
        }
    }
}