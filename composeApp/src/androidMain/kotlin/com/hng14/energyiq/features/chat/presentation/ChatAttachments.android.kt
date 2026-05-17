package com.hng14.energyiq.features.chat.presentation

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenMultipleDocuments
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hng14.energyiq.features.chat.domain.model.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.drawToBitmap
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
actual fun rememberChatAttachmentController(
    onAttachmentsSelected: (List<ChatAttachment>) -> Unit,
): ChatAttachmentController {
    val context = LocalContext.current
    val view = LocalView.current

    val pickerLauncher = rememberLauncherForActivityResult(OpenMultipleDocuments()) { uris ->
        val attachments = uris.map { uri ->
            ChatAttachment(
                id = UUID.randomUUID().toString(),
                name = resolveDisplayName(context, uri),
                type = resolveAttachmentType(context, uri),
                uri = uri.toString(),
            )
        }
        if (attachments.isNotEmpty()) {
            onAttachmentsSelected(attachments)
        }
    }

    return remember(context, view, onAttachmentsSelected, pickerLauncher) {
        ChatAttachmentController(
            pickPhotoOrFiles = {
                pickerLauncher.launch(
                    arrayOf(
                        "image/*",
                        "application/pdf",
                        "text/*",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "*/*",
                    ),
                )
            },
            takeScreenshot = {
                val bitmap = view.rootView.drawToBitmap(Bitmap.Config.ARGB_8888)
                val screenshotFile = File(
                    context.cacheDir,
                    "chat-screenshot-${System.currentTimeMillis()}.png",
                )
                FileOutputStream(screenshotFile).use { output ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                }
                onAttachmentsSelected(
                    listOf(
                        ChatAttachment(
                            id = UUID.randomUUID().toString(),
                            name = screenshotFile.name,
                            type = ChatAttachmentType.SCREENSHOT,
                            uri = Uri.fromFile(screenshotFile).toString(),
                        ),
                    ),
                )
            },
        )
    }
}

private fun resolveDisplayName(
    context: Context,
    uri: Uri,
): String {
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && cursor.moveToFirst()) {
            return cursor.getString(nameIndex) ?: "Attachment"
        }
    }
    return uri.lastPathSegment ?: "Attachment"
}

private fun resolveAttachmentType(
    context: Context,
    uri: Uri,
): ChatAttachmentType {
    val mimeType = context.contentResolver.getType(uri).orEmpty()
    return if (mimeType.startsWith("image/")) {
        ChatAttachmentType.IMAGE
    } else {
        ChatAttachmentType.FILE
    }
}
