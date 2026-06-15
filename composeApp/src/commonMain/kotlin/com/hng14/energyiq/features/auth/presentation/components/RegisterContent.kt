package com.hng14.energyiq.features.auth.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.hng14.energyiq.core.ui.LocalAdaptiveScreenSpec
import com.hng14.energyiq.core.theme.EnergyTheme
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun RegisterContent(
    fullName: String,
    email: String,
    password: String,
    fullNameError: String?,
    emailError: String?,
    passwordError: String?,
    generalError: String?,
    isLoading: Boolean,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onCreateAccount: () -> Unit,
    onGoogleClick: () -> Unit,
    onLoginClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit = {},
    onTermsAndConditionsClick: () -> Unit = {},
) {
    val energyColors = EnergyTheme.colors
    val dmSans = dmSansFontFamily()
    val adaptiveSpec = LocalAdaptiveScreenSpec.current
    val fullNameRuleText = stringResource(Res.string.auth_full_name_rule)
    val fullNameParts = fullName.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    val registerFullNameValid = fullName.isNotEmpty() &&
        fullNameParts.size >= 2 &&
        (fullNameParts.firstOrNull()?.firstOrNull()?.isLetter() == true) &&
        (fullNameParts.drop(1).joinToString(" ").firstOrNull()?.isLetter() == true) &&
        fullNameParts.first().length >= 2 &&
        fullNameParts.drop(1).joinToString(" ").length >= 2
    val registerFullNameError = when {
        fullName.isEmpty() -> fullNameError
        registerFullNameValid -> null
        fullNameParts.size < 2 -> fullNameRuleText
        fullNameParts.firstOrNull()?.firstOrNull()?.isLetter() != true -> stringResource(Res.string.auth_error_first_name_letter)
        fullNameParts.drop(1).joinToString(" ").firstOrNull()?.isLetter() != true -> stringResource(Res.string.auth_error_last_name_letter)
        (fullNameParts.firstOrNull()?.length ?: 0) < 2 -> stringResource(Res.string.auth_error_first_name_short)
        fullNameParts.drop(1).joinToString(" ").length < 2 -> stringResource(Res.string.auth_error_last_name_short)
        else -> fullNameRuleText
    }
    val registerFullNameSupportingText = when {
        fullName.isEmpty() -> null
        registerFullNameValid -> fullNameRuleText
        else -> registerFullNameError
    }
    val registerFullNameSupportingColor = when {
        fullName.isEmpty() -> null
        registerFullNameValid -> Color(0xFF4CD964)
        else -> Color(0xFFD92D20)
    }
    val registerEmailValid = email.isNotEmpty() && email.contains('@') && email.contains('.')
    val registerEmailError = when {
        email.isEmpty() -> emailError
        registerEmailValid -> null
        else -> stringResource(Res.string.auth_error_invalid_email)
    }
    val registerEmailSupportingText = when {
        email.isEmpty() -> null
        registerEmailValid -> stringResource(Res.string.auth_email_valid)
        else -> stringResource(Res.string.auth_error_invalid_email)
    }
    val registerEmailSupportingColor = when {
        email.isEmpty() -> null
        registerEmailValid -> Color(0xFF4CD964)
        else -> Color(0xFFD92D20)
    }
    val passwordRuleText = "Password must be at least 8 characters and a special key"
    val registerPasswordValid = password.isNotEmpty() &&
        password.length >= 8 &&
        password.any { !it.isLetterOrDigit() }
    val registerPasswordError = when {
        password.isEmpty() -> passwordError
        registerPasswordValid -> null
        else -> passwordRuleText
    }
    val registerPasswordSupportingText = when {
        password.isEmpty() -> null
        else -> passwordRuleText
    }
    val registerPasswordSupportingColor = when {
        password.isEmpty() -> null
        registerPasswordValid -> Color(0xFF4CD964)
        else -> Color(0xFFD92D20)
    }
    val createAccountLabel = stringResource(Res.string.auth_create_account)
    val creatingAccountLabel = stringResource(Res.string.auth_creating_account)
    val googleLogoLabel = stringResource(Res.string.auth_google_logo)

    AuthPageLayout(
        backgroundColor = energyColors.appBackground,
        verticalPadding = 20.dp,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AuthBrandHeader()
        RegisterHeader()
        Spacer(modifier = Modifier.height(30.dp))

        AuthTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = stringResource(Res.string.auth_register_name_label),
            placeholder = stringResource(Res.string.auth_register_name_placeholder),
            error = registerFullNameError,
            showSuccess = registerFullNameValid,
            supportingText = registerFullNameSupportingText,
            supportingColor = registerFullNameSupportingColor,
            imeAction = ImeAction.Next,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(18.dp))

        AuthTextField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(Res.string.auth_email_label),
            placeholder = stringResource(Res.string.auth_email_placeholder),
            error = registerEmailError,
            showSuccess = registerEmailValid,
            supportingText = registerEmailSupportingText,
            supportingColor = registerEmailSupportingColor,
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
            error = registerPasswordError,
            showSuccess = registerPasswordValid,
            supportingText = registerPasswordSupportingText,
            supportingColor = registerPasswordSupportingColor,
            showStatusIndicator = false,
            imeAction = ImeAction.Done,
            onImeAction = {},
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onCreateAccount,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(adaptiveSpec.buttonHeight)
                .semantics {
                    contentDescription = if (isLoading) creatingAccountLabel else createAccountLabel
                },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
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
                    text = stringResource(Res.string.auth_create_account),
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
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.social_icon),
                    contentDescription = googleLogoLabel,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Unspecified,
                )
                Spacer(modifier = Modifier.width(10.dp))
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
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.auth_already_have_account),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    letterSpacing = 0.sp,
                ),
                color = Color(0xFF6B7280),
            )
            TextButton(
                onClick = onLoginClick,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
                modifier = Modifier.semantics { role = Role.Button }
            ) {
                Text(
                    text = stringResource(Res.string.auth_log_in),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(Res.string.auth_signup_terms_prefix),
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
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .semantics { role = Role.Button }
                    .clickable(onClick = onTermsAndConditionsClick),
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
                modifier = Modifier.align(Alignment.CenterVertically)
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
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .semantics { role = Role.Button }
                    .clickable(onClick = onPrivacyPolicyClick),
            )
        }
    }
}
