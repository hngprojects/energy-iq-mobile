package com.hng14.energyiq.features.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberGoogleIdTokenLauncher(
    onResult: (Result<String>) -> Unit,
): () -> Unit {
    return remember {
        {
            onResult(Result.failure(UnsupportedOperationException("Google Sign-In is not supported on WASM.")))
        }
    }
}

