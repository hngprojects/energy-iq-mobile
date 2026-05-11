package com.hng14.energyiq.core.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun AuthWaveDecoration(
    modifier: Modifier,
) {
    val context = LocalContext.current
    val waveBitmap = remember(context.applicationContext) {
        cachedWaveBitmap ?: runCatching {
            context.applicationContext.assets.open("wave.png").use { input ->
                BitmapFactory.decodeStream(input)?.asImageBitmap()
            }
        }.onFailure { error ->
            println("Failed to load auth wave decoration from Android assets: ${error.message}")
        }.getOrNull()?.also { bitmap ->
            cachedWaveBitmap = bitmap
            println("Loaded auth wave decoration from Android assets")
        }
    }

    waveBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = modifier,
        )
    }
}

private var cachedWaveBitmap: ImageBitmap? = null
