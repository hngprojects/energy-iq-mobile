package com.hng14.energyiq.features.auth.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.hng14.energyiq.core.theme.EnergyTheme
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.ui.LocalAdaptiveScreenSpec
import com.hng14.energyiq.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmailSentContent(
    generalError: String?,
    isLoading: Boolean,
    onResendLink: () -> Unit,
    onBackToLogin: () -> Unit,
) {
    val dmSans = dmSansFontFamily()
    val adaptiveSpec = LocalAdaptiveScreenSpec.current
    val resendingLinkLabel = stringResource(Res.string.auth_resending_link)
    val resendLinkLabel = stringResource(Res.string.auth_resend_link)

    AuthPageLayout(
        backgroundColor = Color(0xFFFAFAF8),
        verticalPadding = 16.dp,
    ) {
        AuthBrandHeader(topSpacing = 18.dp)
        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = stringResource(Res.string.auth_check_email_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.Bold,
                fontSize = adaptiveSpec.headlineSize,
                lineHeight = 30.sp,
                letterSpacing = 0.sp,
            ),
            color = Color(0xFF1F2430),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = stringResource(Res.string.auth_email_sent_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.Normal,
                fontSize = adaptiveSpec.bodySize,
                lineHeight = 22.sp,
                letterSpacing = 0.sp,
            ),
            color = Color(0xFF7B8190),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.auth_android_links_help),
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.sp,
            ),
            color = Color(0xFF7B8190),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onResendLink,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(adaptiveSpec.buttonHeight)
                .semantics {
                    contentDescription = if (isLoading) resendingLinkLabel else resendLinkLabel
                },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF141D2F),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF141D2F),
                disabledContentColor = Color.White,
            ),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = stringResource(Res.string.auth_resend_link),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.sp,
                    ),
                    color = Color(0xFFF6F6F6),
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth().height(adaptiveSpec.buttonHeight),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.auth_back_to_login),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    letterSpacing = 0.sp,
                ),
                color = Color(0xFF2A2F3C),
            )
        }
    }
}
