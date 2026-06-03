package com.hng14.energyiq.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberImagePicker(
    onImagePicked: (PickedImage) -> Unit,
    onError: (String) -> Unit,
): ImagePickerLauncher {
    // Not implemented on web yet.
    return remember {
        object : ImagePickerLauncher {
            override fun launch() {
                onError("Image picker not supported on this platform yet.")
            }
        }
    }
}

