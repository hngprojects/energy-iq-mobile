package com.hng14.energyiq.core.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ServerErrorDialog(
    message: String,
    onDismiss: () -> Unit,
    title: String = "Something went wrong",
    confirmText: String = "OK",
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        text = { Text(text = message, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF141D2F),
                    contentColor = Color(0xFFF6F6F6),
                    disabledContainerColor = Color(0xFF141D2F),
                    disabledContentColor = Color(0xFFF6F6F6),
                ),
            ) {
                Text(confirmText)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
    )
}
