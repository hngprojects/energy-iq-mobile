package com.hng14.energyiq.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler

@Composable
actual fun PlatformWebView(
    url: String,
    modifier: Modifier,
) {
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(url) {
        uriHandler.openUri(url)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Text("Opening in browser: $url")
    }
}

