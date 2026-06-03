package com.hng14.energyiq.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.dmSansFontFamily
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun GreetingHeader(
    name: String?,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val dmSans = dmSansFontFamily()
    val fullName = name?.trim().orEmpty()
    val firstName = fullName.substringBefore(" ", fullName)
    val displayName = firstName.ifBlank { "User" }

    val greeting = run {
        val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
        when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "$greeting, $displayName!",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF111827)
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = dmSans,
                color = Color(0xFF6B7280)
            )
        )
    }
}
