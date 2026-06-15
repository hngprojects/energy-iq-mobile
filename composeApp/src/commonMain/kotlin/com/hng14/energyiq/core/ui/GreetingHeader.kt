package com.hng14.energyiq.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.theme.EnergyPalette
import com.hng14.energyiq.*
import org.jetbrains.compose.resources.stringResource
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

val greeting: String
    @Composable
    get() {
        val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
        return when (hour) {
            in 5..11 -> stringResource(Res.string.greeting_morning)
            in 12..16 -> stringResource(Res.string.greeting_afternoon)
            else -> stringResource(Res.string.greeting_evening)
        }
    }

@Composable
fun GreetingHeader(
    name: String?,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val dmSans = dmSansFontFamily()
    val fullName = name?.trim().orEmpty()
    val firstName = fullName.substringBefore(" ", fullName)
    val displayName = firstName.ifBlank { stringResource(Res.string.greeting_user_fallback) }

    Column(modifier = modifier) {
        Text(
            text = "$greeting, $displayName!",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = EnergyPalette.TextDark
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = dmSans,
                color = EnergyPalette.TextSecondary
            )
        )
    }
}
