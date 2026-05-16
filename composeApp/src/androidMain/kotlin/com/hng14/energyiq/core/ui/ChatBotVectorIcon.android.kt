package com.hng14.energyiq.core.ui

import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun ChatBotVectorIcon(
    modifier: Modifier,
    contentDescription: String?,
) {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(
        "chat_bot",
        "drawable",
        "com.hng14.energyiq",
    )

    if (resourceId == 0) {
        Icon(
            imageVector = Icons.Outlined.SmartToy,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = Color.White,
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
