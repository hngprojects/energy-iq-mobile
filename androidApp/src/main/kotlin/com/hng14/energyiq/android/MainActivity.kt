package com.hng14.energyiq.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hng14.energyiq.App


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_KotlinStarter)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(applicationContext)
            // App.kt — temporary, remove after testing
//            EmailVerificationScreen(
//                onAction = {}
//            )
        }
    }
}
