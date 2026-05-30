package com.hng14.energyiq.core.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberImagePicker(
    onImagePicked: (PickedImage) -> Unit,
    onError: (String) -> Unit,
): ImagePickerLauncher {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri == null) return@rememberLauncherForActivityResult
            runCatching {
                val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
                val name = queryDisplayName(context, uri) ?: defaultNameFromMime(mime)
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: error("Unable to read image")
                onImagePicked(PickedImage(bytes = bytes, mimeType = mime, fileName = name))
            }.onFailure { e ->
                onError(e.message ?: "Failed to pick image")
            }
        },
    )

    return remember {
        object : ImagePickerLauncher {
            override fun launch() {
                launcher.launch("image/*")
            }
        }
    }
}

private fun queryDisplayName(context: Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?: return null
    cursor.use {
        if (!it.moveToFirst()) return null
        val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (idx < 0) return null
        return it.getString(idx)
    }
}

private fun defaultNameFromMime(mime: String): String = when (mime.lowercase()) {
    "image/png" -> "upload.png"
    "image/webp" -> "upload.webp"
    else -> "upload.jpg"
}

