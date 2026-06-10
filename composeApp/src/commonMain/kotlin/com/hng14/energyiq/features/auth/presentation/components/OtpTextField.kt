package com.hng14.energyiq.features.auth.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.TextStyle
import com.hng14.energyiq.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun OtpTextField(
    otpValue: String, //current OTP string from ViewModel (max 6)
    onOtpChange: (String) -> Unit, //called on every keystroke with the new value
    isError: Boolean = false, //true when the code is incorrect
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val infiniteTransition = rememberInfiniteTransition(label = "otp_cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    val fieldDescription = stringResource(Res.string.auth_otp_field_description)

    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = fieldDescription
            },
        value = otpValue,
        onValueChange = { newValue ->
            if (!enabled) return@BasicTextField
            if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                onOtpChange(newValue)
                if (newValue.length == 6) {
                    keyboardController?.hide()
                }
            }
        },
        enabled = enabled,
        // Always keep OTP input left-to-right even on RTL devices/locales.
        textStyle = TextStyle(textDirection = TextDirection.Ltr),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    repeat(6) { index ->
                        val char = otpValue.getOrNull(index)
                        val isFocused = enabled && index == otpValue.length
                        val borderColor = when {
                            isError -> MaterialTheme.colorScheme.error
                            isFocused || char != null -> Color(0xFF4CD964)
                            else -> MaterialTheme.colorScheme.outline
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .border(
                                    width = if (isFocused) 2.dp else 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isFocused) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(24.dp)
                                        .background(Color(0xFF4CD964).copy(alpha = cursorAlpha))
                                )
                            }
                            Text(
                                text = char?.toString() ?: "",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        },
    )
}
