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
actual fun InverterCardIcon(
    modifier: Modifier,
    contentDescription: String?,
) {
    val context = LocalContext.current
    val iconBitmap = remember(context.applicationContext) {
        cachedInverterCardIcon ?: runCatching {
            context.applicationContext.assets.open("luminus.png").use { input ->
                BitmapFactory.decodeStream(input)?.asImageBitmap()
            }
        }.getOrNull()?.also { bitmap ->
            cachedInverterCardIcon = bitmap
        }
    }

    if (iconBitmap != null) {
        Image(
            bitmap = iconBitmap,
            contentDescription = contentDescription,
            modifier = modifier,
        )
    }
}

private var cachedInverterCardIcon: ImageBitmap? = null
