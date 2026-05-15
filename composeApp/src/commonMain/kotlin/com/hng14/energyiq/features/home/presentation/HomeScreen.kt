package com.hng14.energyiq.features.home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.EnergyPalette
import com.hng14.energyiq.features.home.presentation.components.BatteryCard
import com.hng14.energyiq.features.home.presentation.components.DraggableFab
import com.hng14.energyiq.features.home.presentation.components.EnergyUsageCard
import com.hng14.energyiq.features.home.presentation.components.HomeTopBar
import com.hng14.energyiq.features.home.presentation.components.MetricCard
import com.hng14.energyiq.features.home.presentation.components.PowerUsageCard
import com.hng14.energyiq.features.home.presentation.components.SavingsOverviewCard
import com.hng14.energyiq.features.home.presentation.components.WarningBanner

@Composable
fun HomeScreen() {
    val name = "Amaka Johnson"
    val fullName = name.trim()
    val firstName = fullName.substringBefore(" ", fullName)
    val displayName = firstName.ifBlank { "Amaka" }
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color.White,
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                HomeTopBar(name = name)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Good afternoon, $displayName",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                    ),
                    color = Color(0xFF111827),
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your system has been running\non solar for 6 hrs today.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 16.sp,
                    ),
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(top = 4.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))
                WarningBanner()
                Spacer(modifier = Modifier.height(20.dp))
                MetricCard(
                    title = "Solar Input",
                    value = "4.2 kW",
                    subtitle = "Peak was 5.8 kW at 03:30 pm",
                    showSunIcon = true,
                    badgeText = "Panel working well",
                    badgeColor = Color(0xFFDDF7E6),
                    badgeContentColor = EnergyPalette.BatteryGreen,
                )
                Spacer(modifier = Modifier.height(30.dp))
                BatteryCard()
                Spacer(modifier = Modifier.height(30.dp))
                MetricCard(
                    title = "Running now",
                    value = "2.8 kW",
                    subtitle = "Steady load",
                    showRunningLowIcon = true,
                    wrapSubtitleInContainer = true,
                )
                Spacer(modifier = Modifier.height(30.dp))
                PowerUsageCard()
                Spacer(modifier = Modifier.height(30.dp))
                SavingsOverviewCard()
                Spacer(modifier = Modifier.height(30.dp))
                EnergyUsageCard()
                Spacer(modifier = Modifier.height(18.dp))
            }

            DraggableFab(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 28.dp),
            )
        }
    }
}
