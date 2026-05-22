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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.ui.LocalAdaptiveScreenSpec
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun CheckMailContent(
    email: String,
    isEmailReadOnly: Boolean,
    emailError: String?,
    password: String,
    confirmPassword: String,
    passwordError: String?,
    confirmPasswordError: String?,
    generalError: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onResetPassword: () -> Unit,
    onBackToLogin: () -> Unit,
) {
    val dmSans = dmSansFontFamily()
    val adaptiveSpec = LocalAdaptiveScreenSpec.current
    val passwordRuleText = stringResource(Res.string.auth_password_rule)
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
        else -> stringResource(Res.string.auth_passwords_do_not_match)
    }

    AuthPageLayout(
        backgroundColor = Color(0xFFFAFAF8),
        verticalPadding = 16.dp,
    ) {
        AuthBrandHeader(topSpacing = 18.dp)
        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = stringResource(Res.string.auth_reset_title),
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
            text = if (email.isBlank()) {
                stringResource(Res.string.auth_reset_desc_no_email)
            } else {
                stringResource(Res.string.auth_reset_desc_with_email, email)
            },
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

        AuthTextField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(Res.string.auth_email_label),
            placeholder = stringResource(Res.string.auth_email_placeholder),
            error = emailError,
            showSuccess = email.isNotBlank() && emailError == null,
            imeAction = ImeAction.Next,
            readOnly = isEmailReadOnly,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(18.dp))

        PasswordTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = stringResource(Res.string.auth_new_password),
            placeholder = stringResource(Res.string.auth_new_password_placeholder),
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
            label = stringResource(Res.string.auth_confirm_new_password),
            placeholder = stringResource(Res.string.auth_new_password_placeholder),
            error = resolvedConfirmPasswordError,
            showSuccess = confirmPasswordValid,
            supportingText = when {
                confirmPassword.isEmpty() -> null
                confirmPasswordValid -> stringResource(Res.string.auth_passwords_match)
                else -> stringResource(Res.string.auth_passwords_do_not_match)
            },
            supportingColor = if (confirmPasswordValid) Color(0xFF4CD964) else Color(0xFFF3A847),
            showStatusIndicator = false,
            imeAction = ImeAction.Done,
            onImeAction = {},
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
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = stringResource(Res.string.auth_reset_password),
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
                text = stringResource(Res.string.auth_back_to_login_title),
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
