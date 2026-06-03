package com.hng14.energyiq.core.ui

import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.ImageViewCompat

@Composable
actual fun CasIcon(
    modifier: Modifier,
    contentDescription: String?,
    tint: Color?,
) {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(
        "cas_icon",
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
                setImageDrawable(AppCompatResources.getDrawable(viewContext, resourceId))
                this.contentDescription = contentDescription
                if (tint != null) {
                    ImageViewCompat.setImageTintList(
                        this,
                        android.content.res.ColorStateList.valueOf(tint.toArgb()),
                    )
                }
            }
        },
        update = { view ->
            if (tint != null) {
                ImageViewCompat.setImageTintList(
                    view,
                    android.content.res.ColorStateList.valueOf(tint.toArgb()),
                )
            } else {
                ImageViewCompat.setImageTintList(view, null)
            }
            view.contentDescription = contentDescription
        }
    )
}
