package com.hng14.energyiq.core.ui

import androidx.compose.runtime.Composable

data class PickedImage(
    val bytes: ByteArray,
    val mimeType: String,
    val fileName: String = "upload.jpg",
)

interface ImagePickerLauncher {
    fun launch()
}

/**
 * Platform image picker. Android implementation opens a system picker and returns the file bytes.
 * Other platforms may provide a no-op implementation for now.
 */
@Composable
expect fun rememberImagePicker(
    onImagePicked: (PickedImage) -> Unit,
    onError: (String) -> Unit = {},
): ImagePickerLauncher

