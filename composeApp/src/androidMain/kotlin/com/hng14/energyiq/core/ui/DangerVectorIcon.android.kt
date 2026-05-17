package com.hng14.energyiq.core.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources

@Composable
actual fun DangerVectorIcon(
    modifier: Modifier,
    contentDescription: String?,
    tint: Color,
) {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(
        "danger_vector",
        "drawable",
        context.packageName,
    )

    if (resourceId == 0) {
        Spacer(modifier = modifier)
        return
    }

    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            ImageView(viewContext).apply {
                val drawable = AppCompatResources.getDrawable(viewContext, resourceId)?.mutate()
                drawable?.setTint(tint.toArgb())
                setImageDrawable(drawable)
                this.contentDescription = contentDescription
            }
        },
        update = { view ->
            val drawable = view.drawable?.mutate()
            drawable?.setTint(tint.toArgb())
            view.contentDescription = contentDescription
        }
    )
}
