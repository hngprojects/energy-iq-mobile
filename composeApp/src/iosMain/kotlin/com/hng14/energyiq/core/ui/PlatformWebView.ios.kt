package com.hng14.energyiq.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration

@Composable
actual fun PlatformWebView(
    url: String,
    modifier: Modifier,
) {
    val webView = remember {
        WKWebView(
            frame = CGRectZero,
            configuration = WKWebViewConfiguration(),
        )
    }

    LaunchedEffect(url) {
        val nsUrl = NSURL(string = url) ?: return@LaunchedEffect
        webView.loadRequest(NSURLRequest(nsUrl))
    }

    UIKitView(
        modifier = modifier,
        factory = { webView },
    )
}

