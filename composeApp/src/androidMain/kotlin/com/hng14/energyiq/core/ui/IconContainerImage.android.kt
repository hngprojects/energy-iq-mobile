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
actual fun IconContainerImage(
    modifier: Modifier,
    contentDescription: String?,
) {
    val context = LocalContext.current
    val imageBitmap = remember(context.applicationContext) {
        cachedIconContainerBitmap ?: runCatching {
            context.applicationContext.assets.open("icon_container.png").use { input ->
                BitmapFactory.decodeStream(input)?.asImageBitmap()
            }
        }.getOrNull()?.also { bitmap ->
            cachedIconContainerBitmap = bitmap
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

private var cachedIconContainerBitmap: ImageBitmap? = null
