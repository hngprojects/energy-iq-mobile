package com.hng14.energyiq.features.auth.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.ui.LocalAdaptiveScreenSpec
import com.hng14.energyiq.core.theme.dmSansFontFamily

@Composable
fun CheckMailContent(
    email: String,
    password: String,
    confirmPassword: String,
    passwordError: String?,
    confirmPasswordError: String?,
    isLoading: Boolean,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onResetPassword: () -> Unit,
    onBackToLogin: () -> Unit,
) {
    val dmSans = dmSansFontFamily()
    val adaptiveSpec = LocalAdaptiveScreenSpec.current
    val passwordRuleText = "Password must be at least 8 characters and a special key"
    val passwordValid = password.isNotEmpty() &&
        password.length >= 8 &&
        password.any { !it.isLetterOrDigit() }
    val confirmPasswordValid = confirmPassword.isNotEmpty() &&
        confirmPassword == password &&
        passwordValid
    val resolvedPasswordError = when {
        password.isEmpty() -> passwordError
        passwordValid -> null
        else -> passwordRuleText
    }
    val resolvedConfirmPasswordError = when {
        confirmPassword.isEmpty() -> confirmPasswordError
        confirmPasswordValid -> null
        else -> "Passwords do not match"
    }

    AuthPageLayout(
        backgroundColor = Color(0xFFFAFAF8),
        verticalPadding = 16.dp,
    ) {
        AuthBrandHeader(topSpacing = 18.dp)
        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Check your email",
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
            text = "Create a new password for your\n$email EnergyIQ\naccount.",
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

        Spacer(modifier = Modifier.height(28.dp))

        PasswordTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "New Password",
            placeholder = "Enter your password",
            error = resolvedPasswordError,
            showSuccess = passwordValid,
            supportingText = if (password.isEmpty()) null else passwordRuleText,
            supportingColor = if (passwordValid) Color(0xFF4CD964) else Color(0xFFF3A847),
            showStatusIndicator = false,
            imeAction = ImeAction.Next,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(18.dp))

        PasswordTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirm New Password",
            placeholder = "Enter your password",
            error = resolvedConfirmPasswordError,
            showSuccess = confirmPasswordValid,
            supportingText = when {
                confirmPassword.isEmpty() -> null
                confirmPasswordValid -> "Passwords match"
                else -> "Passwords do not match"
            },
            supportingColor = if (confirmPasswordValid) Color(0xFF4CD964) else Color(0xFFF3A847),
            showStatusIndicator = false,
            imeAction = ImeAction.Done,
            onImeAction = onResetPassword,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onResetPassword,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(adaptiveSpec.buttonHeight),
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
                    modifier = Modifier.height(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = "Reset Password",
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
                text = "Back to Login",
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
