package com.hng14.energyiq.features.auth.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.ui.LocalAdaptiveScreenSpec
import com.hng14.energyiq.core.theme.EnergyTheme
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginContent(
    email: String,
    password: String,
    emailError: String?,
    passwordError: String?,
    generalError: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleMode: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoogleClick: () -> Unit,
) {
    val energyColors = EnergyTheme.colors
    val dmSans = dmSansFontFamily()
    val adaptiveSpec = LocalAdaptiveScreenSpec.current
    val rememberPassword = remember { mutableStateOf(false) }
    val emailLooksValid = email.isNotBlank() && email.contains('@') && emailError == null
    val passwordLooksValid = password.length >= 8 && password.any { !it.isLetterOrDigit() } && passwordError == null

    AuthPageLayout(
        backgroundColor = energyColors.appBackground,
        verticalPadding = 20.dp,
    ) {
        AuthBrandHeader()
        Spacer(modifier = Modifier.height(34.dp))

        Text(
            text = stringResource(Res.string.auth_welcome_back),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = adaptiveSpec.headlineSize,
                letterSpacing = 0.sp,
            ),
            color = Color(0xFF2A2F3C),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(Res.string.auth_login_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.Normal,
                fontSize = adaptiveSpec.bodySize,
                lineHeight = 21.sp,
                letterSpacing = 0.sp,
            ),
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(30.dp))

        AuthTextField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(Res.string.auth_email_label),
            placeholder = stringResource(Res.string.auth_email_placeholder),
            error = emailError,
            showSuccess = emailLooksValid,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(18.dp))

        PasswordTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = stringResource(Res.string.auth_password_label),
            placeholder = stringResource(Res.string.auth_password_placeholder),
            error = passwordError,
            showSuccess = passwordLooksValid,
            showStatusIndicator = false,
            imeAction = ImeAction.Done,
            onImeAction = {},
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.clickable { rememberPassword.value = !rememberPassword.value },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    modifier = Modifier.size(14.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = if (rememberPassword.value) Color(0xFFF3A847) else Color.Transparent,
                    border = BorderStroke(1.dp, Color(0xFFF3A847)),
                ) {
                    if (rememberPassword.value) {
                        Row(
                            modifier = Modifier.fillMaxWidth().height(14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Remember password enabled",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp),
                            )
                        }
                    }
                }

                Text(
                    text = stringResource(Res.string.auth_remember_password),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        letterSpacing = 0.sp,
                    ),
                    color = Color(0xFF9CA3AF),
                )
            }

            TextButton(
                onClick = onForgotPasswordClick,
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(
                    text = stringResource(Res.string.auth_forgot_password),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        letterSpacing = 0.sp,
                    ),
                    color = Color(0xFFF3A847),
                )
            }
        }

        generalError?.let { error ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = error,
                color = energyColors.danger,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = onSubmit,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(adaptiveSpec.buttonHeight),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF141D2F),
                contentColor = Color(0xFFF6F6F6),
                disabledContainerColor = Color(0xFF141D2F),
                disabledContentColor = Color(0xFFF6F6F6),
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
                    text = stringResource(Res.string.auth_sign_in),
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

        Spacer(modifier = Modifier.height(18.dp))
        AuthDivider()
        Spacer(modifier = Modifier.height(18.dp))

        OutlinedButton(
            onClick = onGoogleClick,
            modifier = Modifier.fillMaxWidth().height(adaptiveSpec.buttonHeight),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.auth_continue_google),
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

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.auth_no_account),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    letterSpacing = 0.sp,
                ),
                color = Color(0xFF6B7280),
            )
            Spacer(modifier = Modifier.width(6.dp))
            TextButton(
                onClick = onToggleMode,
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(
                    text = stringResource(Res.string.auth_create_one),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        letterSpacing = 0.sp,
                    ),
                    color = Color(0xFFF3A847),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(Res.string.auth_terms_prefix),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    letterSpacing = 0.sp,
                ),
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.auth_terms_of_service),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    letterSpacing = 0.sp,
                ),
                color = Color(0xFF2A2F3C),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.auth_and),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    letterSpacing = 0.sp,
                ),
                color = Color(0xFF6B7280),
            )
            Text(
                text = stringResource(Res.string.auth_privacy_policy),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    letterSpacing = 0.sp,
                ),
                color = Color(0xFF2A2F3C),
                textAlign = TextAlign.Center,
            )
        }
    }
}
