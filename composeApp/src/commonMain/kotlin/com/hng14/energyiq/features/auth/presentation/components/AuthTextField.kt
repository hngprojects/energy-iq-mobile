package com.hng14.energyiq.features.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.hng14.energyiq.*
import com.hng14.energyiq.core.theme.dmSansFontFamily
import org.jetbrains.compose.resources.stringResource

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    error: String? = null,
    showSuccess: Boolean = false,
    supportingText: String? = null,
    supportingColor: Color? = null,
    showStatusIndicator: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    readOnly: Boolean = false,
) {
    val dmSans = dmSansFontFamily()
    val fieldShape = RoundedCornerShape(10.dp)
    val borderColor = when {
        error != null -> MaterialTheme.colorScheme.error
        showSuccess -> Color(0xFF4CD964)
        else -> Color(0xFFD8DBE2)
    }
    val textStyle = MaterialTheme.typography.bodyMedium.copy(
        fontFamily = dmSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Start,
        color = Color(0xFF2A2F3C),
    )
    val placeholderStyle = textStyle.copy(
        fontFamily = dmSans,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Start,
        color = Color(0xFF9CA3AF),
    )

    val fieldDescription = stringResource(Res.string.auth_input_field_description, label)
    val validDescription = stringResource(Res.string.auth_valid)

    Column(
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = label
        }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                letterSpacing = 0.sp,
            ),
            color = Color(0xFF2A2F3C),
            modifier = Modifier.padding(bottom = 8.dp),
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = fieldDescription
                },
            textStyle = textStyle,
            readOnly = readOnly,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
            ),
            keyboardActions = KeyboardActions(onAny = { onImeAction() }),
            singleLine = true,
            cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            color = Color(0xFFFCFCFC),
                            shape = fieldShape,
                        )
                        .border(
                            width = 1.dp,
                            color = borderColor,
                            shape = fieldShape,
                        )
                        .padding(horizontal = 28.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (value.isEmpty() && placeholder.isNotEmpty()) {
                            Text(
                                text = placeholder,
                                style = placeholderStyle,
                            )
                        }
                        innerTextField()
                    }

                    if (showSuccess && showStatusIndicator) {
                        SuccessIndicator(validDescription)
                    }
                }
            },
        )

        when {
            supportingText != null -> {
                Text(
                    text = supportingText,
                    color = supportingColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                )
            }

            error != null -> {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                )
            }
        }
    }
}

@Composable
private fun SuccessIndicator(contentDescription: String) {
    Surface(
        modifier = Modifier.size(width = 15.dp, height = 14.dp),
        shape = RoundedCornerShape(999.dp),
        color = Color(0xFF4CD964),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(10.dp),
            )
        }
    }
}
