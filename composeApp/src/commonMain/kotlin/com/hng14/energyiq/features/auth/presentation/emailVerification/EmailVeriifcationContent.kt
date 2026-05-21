package com.hng14.energyiq.features.auth.presentation.emailVerification

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.ui.LocalAdaptiveScreenSpec
import com.hng14.energyiq.core.ui.VerificationSuccessIcon
import com.hng14.energyiq.features.auth.presentation.components.AuthBrandHeader
import com.hng14.energyiq.features.auth.presentation.components.AuthPageLayout
import com.hng14.energyiq.features.auth.presentation.components.OtpTextField

private val SuccessGreen = Color(0xFF4CD964)
private val SuccessRipple = Color(0xFFBFEFCA)


@Composable
fun EmailVerificationContent(
    firstName: String,
    otpValue: String,
    onOtpChange: (String) -> Unit,
    isError: Boolean = false,
    isVerifying: Boolean = false,
    errorMessage: String? = null,
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
            enabled = !isVerifying,
        )

        if (isError) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = errorMessage?.takeIf { it.isNotBlank() }
                    ?: "The code you entered is incorrect.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
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
    firstName: String,
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
        Spacer(Modifier.height(100.dp))
        RippleSuccessIllustration()
        Spacer(Modifier.height(36.dp))

        Text(
            text = "Email Verification\nSuccessful",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = adaptiveSpec.headlineSize,
                fontWeight = FontWeight.SemiBold,
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = "Hello $firstName, your email has been successfully verified.",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.W400,
                fontSize = adaptiveSpec.bodySize,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

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
private fun RippleSuccessIllustration() {
    val transition = rememberInfiniteTransition(label = "verification_success_ripple")
    val outerScale = transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.42f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2100),
            repeatMode = RepeatMode.Restart,
        ),
        label = "outer_scale",
    )
    val outerAlpha = transition.animateFloat(
        initialValue = 0.42f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2100),
            repeatMode = RepeatMode.Restart,
        ),
        label = "outer_alpha",
    )
    val innerScale = transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2100, delayMillis = 280),
            repeatMode = RepeatMode.Restart,
        ),
        label = "inner_scale",
    )
    val innerAlpha = transition.animateFloat(
        initialValue = 0.34f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2100, delayMillis = 280),
            repeatMode = RepeatMode.Restart,
        ),
        label = "inner_alpha",
    )

    Box(
        modifier = Modifier.size(176.dp),
        contentAlignment = Alignment.Center,
    ) {
        RippleCircle(scale = outerScale.value, alpha = outerAlpha.value, size = 176.dp)
        RippleCircle(scale = innerScale.value, alpha = innerAlpha.value, size = 148.dp)

        VerificationSuccessIcon(
            modifier = Modifier.size(82.dp),
            contentDescription = "Verification successful",
        )
    }
}

@Composable
private fun RippleCircle(
    scale: Float,
    alpha: Float,
    size: androidx.compose.ui.unit.Dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .background(SuccessRipple, CircleShape),
    )
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
