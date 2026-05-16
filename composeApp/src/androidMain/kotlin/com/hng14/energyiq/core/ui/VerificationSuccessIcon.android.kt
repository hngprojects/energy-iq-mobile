package com.hng14.energyiq.core.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@Composable
actual fun VerificationSuccessIcon(
    modifier: Modifier,
    contentDescription: String?,
) {
    val context = LocalContext.current
    val drawableId = remember(context) {
        context.resources.getIdentifier(
            "success_icon",
            "drawable",
            context.packageName,
        )
    }

    if (drawableId != 0) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = contentDescription,
            modifier = modifier,
        )
    }
}
