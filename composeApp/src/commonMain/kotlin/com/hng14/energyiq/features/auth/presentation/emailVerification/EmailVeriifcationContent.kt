package com.hng14.energyiq.features.auth.presentation.emailVerification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.EnergyPalette
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.ui.LocalAdaptiveScreenSpec
import com.hng14.energyiq.features.auth.presentation.components.AuthBrandHeader
import com.hng14.energyiq.features.auth.presentation.components.AuthPageLayout
import com.hng14.energyiq.features.auth.presentation.components.OtpTextField

//private val DarkButton = Color(0xFF141D2F)
//private val ErrorRed = Color(0xFFEF4444)
private val SuccessGreen = Color(0xFF22C55E)
private val SuccessGreenLight = Color(0xFFDCFCE7)


@Composable
fun EmailVerificationContent(
    firstName: String,
    otpValue: String,
    onOtpChange: (String) -> Unit,
    isError: Boolean = false,
    isVerifying: Boolean = false,
    onVerifyClick: () -> Unit,
    onBackToSignUp: () -> Unit,
) {

    val adaptiveSpec = LocalAdaptiveScreenSpec.current
    val dmSans = dmSansFontFamily()

    AuthPageLayout(
        backgroundColor = MaterialTheme.colorScheme.background,
        verticalPadding = 24.dp,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AuthBrandHeader()

        VerificationHeader(
            "Email Verification",
            "Hello $firstName, enter the 6-digit code sent to your email to verify and activate your EnergyIQ account.",
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Please paste(or type) your 6-digit code",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))

        OtpTextField(
            otpValue = otpValue,
            onOtpChange = onOtpChange,
            isError = isError,
        )

        if (isError) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⚠ Oh no! The code you entered is incorrect.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onVerifyClick,
            enabled = otpValue.length == 6 && !isVerifying,
            modifier = Modifier
                .fillMaxWidth()
                .height(adaptiveSpec.buttonHeight),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF141D2F),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF141D2F),
                disabledContentColor = Color.White,
            ),
        ) {
            if (isVerifying) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = "Verify my Email",
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

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackToSignUp,
            modifier = Modifier
                .fillMaxWidth()
                .height(adaptiveSpec.buttonHeight),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = "Back to Sign Up",
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

@Composable
fun EmailVerificationSuccessContent(
    onContinue: () -> Unit,
) {
    val adaptiveSpec = LocalAdaptiveScreenSpec.current
    val dmSans = dmSansFontFamily()
    AuthPageLayout(
        backgroundColor = MaterialTheme.colorScheme.background,
        verticalPadding = 24.dp,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AuthBrandHeader()
        VerificationHeader(
            "Email Verification\nSuccessful",
            "Hello Amaka, your email has been successfully verified.",
        )

        Spacer(Modifier.weight(1f))

        // Success icon , is an image, going to replace this
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(SuccessGreenLight, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(52.dp),
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(adaptiveSpec.buttonHeight),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF141D2F),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF141D2F),
                disabledContentColor = Color.White,
            ),
        ) {
            Text(
                text = "Continue",
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
}

@Composable
fun VerificationHeader(
    title: String,
    subtitle: String,
) {
    val adaptiveSpec = LocalAdaptiveScreenSpec.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = adaptiveSpec.headlineSize),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.W400,
                fontSize = adaptiveSpec.bodySize,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}