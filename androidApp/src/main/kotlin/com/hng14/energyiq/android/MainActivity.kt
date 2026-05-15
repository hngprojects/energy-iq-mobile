package com.hng14.energyiq.android

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hng14.energyiq.App

class MainActivity : ComponentActivity() {
    companion object {
        private const val Tag = "EnergyIQDeepLink"
    }

    private var incomingLink by mutableStateOf<String?>(null)
    private var incomingLinkId by mutableLongStateOf(0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_KotlinStarter)
        super.onCreate(savedInstanceState)
        Log.d(Tag, "onCreate intent=${intent?.dataString}")
        updateIncomingLink(intent?.dataString)
        enableEdgeToEdge()
        setContent {
            App(
                context = applicationContext,
                incomingLink = incomingLink,
                incomingLinkId = incomingLinkId,
            )
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        Log.d(Tag, "onNewIntent intent=${intent.dataString}")
        updateIncomingLink(intent.dataString)
    }

    private fun updateIncomingLink(url: String?) {
        Log.d(Tag, "updateIncomingLink url=$url nextId=${incomingLinkId + 1L}")
        incomingLink = url
        incomingLinkId += 1L
    }
}
