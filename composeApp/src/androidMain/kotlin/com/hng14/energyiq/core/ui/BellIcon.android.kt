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
actual fun BellIcon(
    modifier: Modifier,
    contentDescription: String?,
) {
    val context = LocalContext.current
    val imageBitmap = remember(context.applicationContext) {
        cachedBellBitmap ?: runCatching {
            context.applicationContext.assets.open("bell.png").use { input ->
                BitmapFactory.decodeStream(input)?.asImageBitmap()
            }
        }.getOrNull()?.also { bitmap ->
            cachedBellBitmap = bitmap
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = contentDescription,
            modifier = modifier,
        )
    }
}

private var cachedBellBitmap: ImageBitmap? = null
