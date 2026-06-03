package com.hng14.energyiq.features.costAndSavings.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.ui.GreetingHeader
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.costAndSavings.presentation.components.SavingsInsightBanner
import com.hng14.energyiq.features.costAndSavings.presentation.components.SavingsTrendChart
import com.hng14.energyiq.features.costAndSavings.presentation.components.StepperButton
import com.hng14.energyiq.features.costAndSavings.presentation.components.StepperInput
import com.hng14.energyiq.features.costAndSavings.presentation.components.SummaryStatCard
import com.hng14.energyiq.features.home.presentation.components.BackHomeTopBar
import org.koin.compose.koinInject

@Composable
fun CostAndSavingsScreen(
    onBack: () -> Unit,
) {
    val authRepository = koinInject<AuthRepository>()
    var userName by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        userName = authRepository.getCurrentUser()?.name
    }

    val state by remember { mutableStateOf(CostAndSavingsUiState()) }
    val scrollState = rememberScrollState()
    val dmSans = dmSansFontFamily()

    // Calculator State
    var pmsPrice by remember { mutableStateOf(1000.0) }
    var fuelRate by remember { mutableStateOf(2.5) }
    var hoursUsed by remember { mutableStateOf(8) }
    var tariffBand by remember { mutableStateOf("A") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = Color.White,
        topBar = {
            BackHomeTopBar(
                title = "Cost and Savings",
                name = userName,
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            GreetingHeader(
                name = userName,
                subtitle = "Here's your energy summary today"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Summary Stats Grid (Mobile 2x2)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryStatCard(
                        title = "Total Saved",
                        value = state.totalSaved,
                        trend = state.totalSavedTrend,
                        icon = { },
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        title = "Generator Cost",
                        value = state.generatorCostAvoided,
                        trend = state.generatorTrend,
                        icon = {  },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryStatCard(
                        title = "Energy Consumed",
                        value = state.energyConsumed,
                        trend = state.energyTrend,
                        icon = { },
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        title = "Generation Today",
                        value = state.generationToday,
                        trend = state.generationRemaining,
                        icon = { },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Calculator Section
            Text(
                text = "Calculator",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF111827)
            )
            Text(
                text = "Energy Savings Calculator see how much you save with solar",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = dmSans,
                    color = Color(0xFF6B7280)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                StepperInput(
                    label = "PMS Pump Price Per Litre",
                    value = "₦${pmsPrice.toInt()}",
                    onDecrement = { if (pmsPrice > 0) pmsPrice -= 50 },
                    onIncrement = { pmsPrice += 50 }
                )
                StepperInput(
                    label = "Generator fuel consumption rate",
                    value = "$fuelRate L/hr",
                    onDecrement = { if (fuelRate > 0.1) fuelRate = (fuelRate - 0.1).coerceAtLeast(0.1) },
                    onIncrement = { fuelRate += 0.1 }
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Hours used per day",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        ),
                        color = Color(0xFF111827)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF9FAFB)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "$hoursUsed hrs",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = dmSans,
                                    color = Color(0xFF111827),
                                    fontSize = 16.sp
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StepperButton(
                                    icon = { Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(20.dp)) },
                                    onClick = { if (hoursUsed > 0) hoursUsed-- }
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                StepperButton(
                                    icon = { Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(20.dp)) },
                                    onClick = { if (hoursUsed < 24) hoursUsed++ }
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "NERC Tariff Band (A/B/C)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        ),
                        color = Color(0xFF111827)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF9FAFB)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Band $tariffBand",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = dmSans,
                                    color = Color(0xFF111827),
                                    fontSize = 16.sp
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val bands = listOf("A", "B", "C")
                                StepperButton(
                                    icon = { Icon(Icons.Default.Remove, contentDescription = "Previous Band", modifier = Modifier.size(20.dp)) },
                                    onClick = {
                                        val currentIndex = bands.indexOf(tariffBand)
                                        if (currentIndex > 0) tariffBand = bands[currentIndex - 1]
                                    }
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                StepperButton(
                                    icon = { Icon(Icons.Default.Add, contentDescription = "Next Band", modifier = Modifier.size(20.dp)) },
                                    onClick = {
                                        val currentIndex = bands.indexOf(tariffBand)
                                        if (currentIndex < bands.size - 1) tariffBand = bands[currentIndex + 1]
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier
                        .size(width = 156.dp, height = 52.dp)
                        .clickable { /* Calculate logic */ },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF111827),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Check Calculator",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = dmSans,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                            ),
                            color = Color.White,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Savings Trend Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Savings Trend",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF111827)
                    )
                    Text(
                        text = "Daily total savings compared to generator",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = dmSans,
                            color = Color(0xFF6B7280)
                        )
                    )
                }

                // Timeframe Filter
                var expanded by remember { mutableStateOf(false) }
                var selectedTimeframe by remember { mutableStateOf("Daily") }
                val timeframes = listOf("Daily", "Weekly", "Monthly")

                Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                    Surface(
                        modifier = Modifier
                            .clickable { expanded = true },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF111827)
                            )
                            Text(
                                text = selectedTimeframe,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = dmSans,
                                    color = Color(0xFF111827)
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF6B7280)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        timeframes.forEach { timeframe ->
                            DropdownMenuItem(
                                text = { Text(timeframe, fontFamily = dmSans) },
                                onClick = {
                                    selectedTimeframe = timeframe
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Line Chart
            SavingsTrendChart(
                dataPoints = listOf(15000f, 18000f, 14000f, 17000f, 22000f, 19500f, 21000f),
                labels = listOf("Mon 12", "Tue 13", "Wed 14", "Thu 15", "Fri 16", "Sat 17", "Sun 18"),
                fontFamily = dmSans
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SavingsInsightBanner(text = "Your savings increased by 20% due to high solar output")

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
