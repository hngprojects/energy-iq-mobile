package com.hng14.energyiq.core.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources

@Composable
actual fun DangerVectorIcon(
    modifier: Modifier,
    contentDescription: String?,
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
                setImageDrawable(AppCompatResources.getDrawable(viewContext, resourceId))
                this.contentDescription = contentDescription
            }
        },
    )
}
