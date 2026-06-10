package com.hng14.energyiq.features.auth.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.hng14.energyiq.core.ui.LocalAdaptiveScreenSpec
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ResetSuccessContent(
    onSignIn: () -> Unit,
) {
    val dmSans = dmSansFontFamily()
    val adaptiveSpec = LocalAdaptiveScreenSpec.current

    AuthPageLayout(
        backgroundColor = Color(0xFFFAFAF8),
        verticalPadding = 16.dp,
    ) {
        AuthBrandHeader(topSpacing = 18.dp)
        Spacer(modifier = Modifier.height(260.dp))

        Text(
            text = stringResource(Res.string.auth_reset_success_title),
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
            text = stringResource(Res.string.auth_reset_success_subtitle),
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

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onSignIn,
            modifier = Modifier
                .fillMaxWidth()
                .height(adaptiveSpec.buttonHeight)
                .semantics { role = Role.Button },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF141D2F),
                contentColor = Color.White,
            ),
        ) {
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
}
