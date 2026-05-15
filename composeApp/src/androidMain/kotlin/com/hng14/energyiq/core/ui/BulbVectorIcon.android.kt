package com.hng14.energyiq.core.ui

import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun BulbVectorIcon(
    modifier: Modifier,
    contentDescription: String?,
) {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(
        "bulb",
        "drawable",
        "com.hng14.energyiq",
    )

    if (resourceId == 0) {
        Icon(
            imageVector = Icons.Outlined.Lightbulb,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = Color(0xFF1F2937),
        )
    } else {
        AndroidView(
            modifier = modifier,
            factory = { viewContext ->
                ImageView(viewContext).apply {
                    setImageDrawable(AppCompatResources.getDrawable(viewContext, resourceId))
                    this.contentDescription = contentDescription
                }
            },
        )
    }
}
