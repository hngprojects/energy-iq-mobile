package com.hng14.energyiq.features.auth.presentation

import androidx.compose.runtime.Composable

/**
 * Platform-specific Google Sign-In.
 *
 * Android: launches Google Sign-In UI and returns an `idToken`.
 * Other targets: currently unsupported and will return a failure.
 */
@Composable
expect fun rememberGoogleIdTokenLauncher(
    onResult: (Result<String>) -> Unit,
): () -> Unit

