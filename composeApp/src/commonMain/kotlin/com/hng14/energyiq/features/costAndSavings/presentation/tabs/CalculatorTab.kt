package com.hng14.energyiq.features.costAndSavings.presentation.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.features.costAndSavings.presentation.components.CalculatorStepIndicator
import com.hng14.energyiq.features.costAndSavings.presentation.components.PeriodOption
import com.hng14.energyiq.features.costAndSavings.presentation.components.PeriodSelectionCard
import com.hng14.energyiq.features.costAndSavings.presentation.components.StepperButton
import com.hng14.energyiq.features.costAndSavings.presentation.components.StepperInput

@Composable
fun CalculatorTab(
    dmSans: FontFamily,
    onDateRangePickRequest: () -> Unit,
    customRangeDescription: String
) {
    var currentStep by remember { mutableStateOf(1) }
    var selectedPeriod by remember { mutableStateOf("This Week") }

    // Calculator State
    var pmsPrice by remember { mutableStateOf(1000.0) }
    var fuelRate by remember { mutableStateOf(2.5) }
    var hoursUsed by remember { mutableStateOf(8) }
    var tariffBand by remember { mutableStateOf("A") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Calculator Savings",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.Bold
            ),
            color = Color(0xFF111827)
        )
        Text(
            text = "Step $currentStep of 3",
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = dmSans,
                color = Color(0xFFF59E0B),
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        CalculatorStepIndicator(currentStep = currentStep, fontFamily = dmSans)

        Spacer(modifier = Modifier.height(32.dp))

        when (currentStep) {
            1 -> {
                Text(
                    text = "Select Calculation Period",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF111827)
                )
                Text(
                    text = "Choose the time period you want to calculate your savings for.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = dmSans,
                        color = Color(0xFF6B7280)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Period Cards Grid
                val periods = listOf(
                    PeriodOption("This Week", "May 31, 2026 - Jun 6, 2026"),
                    PeriodOption("This Month", "Jun 1, 2026 - Jun 30, 2026"),
                    PeriodOption("Last Month", "May 1, 2026 - May 31, 2026"),
                    PeriodOption("Custom Range", customRangeDescription)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    periods.take(2).forEach { period ->
                        PeriodSelectionCard(
                            option = period,
                            isSelected = selectedPeriod == period.title,
                            onClick = { 
                                selectedPeriod = period.title 
                                if (period.title == "Custom Range") onDateRangePickRequest()
                            },
                            modifier = Modifier.weight(1f),
                            fontFamily = dmSans
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    periods.drop(2).forEach { period ->
                        PeriodSelectionCard(
                            option = period,
                            isSelected = selectedPeriod == period.title,
                            onClick = { 
                                selectedPeriod = period.title 
                                if (period.title == "Custom Range") onDateRangePickRequest()
                            },
                            modifier = Modifier.weight(1f),
                            fontFamily = dmSans
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Redesigned Info Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, Color(0xFFFEF3C7))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Circular Info Icon
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = Color(0xFFF59E0B)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "!",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Why does the period matter?",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = "Your savings are calculated based on your energy usage, fuel price, and tariff for the selected period",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = dmSans,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                ),
                                color = Color(0xFF6B7280)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Refined Calendar Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.size(width = 44.dp, height = 48.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(14.dp)
                                        .background(Color(0xFFF59E0B), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "JUNE",
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "5",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            fontFamily = dmSans
                                        ),
                                        color = Color(0xFF111827)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Continue Button
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Button(
                        onClick = { currentStep = 2 },
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF111827),
                            contentColor = Color.White
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Continue", fontFamily = dmSans)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
            2 -> {
                // Step 2 content
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
                        onDecrement = {
                            if (fuelRate > 0.1) fuelRate = (fuelRate - 0.1).coerceAtLeast(0.1)
                        },
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
                                        icon = {
                                            Icon(
                                                Icons.Default.Remove,
                                                contentDescription = "Decrease",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        onClick = { if (hoursUsed > 0) hoursUsed-- }
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    StepperButton(
                                        icon = {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = "Increase",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
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
                                        icon = {
                                            Icon(
                                                Icons.Default.Remove,
                                                contentDescription = "Previous Band",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        onClick = {
                                            val currentIndex = bands.indexOf(tariffBand)
                                            if (currentIndex > 0) tariffBand =
                                                bands[currentIndex - 1]
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    StepperButton(
                                        icon = {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = "Next Band",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        onClick = {
                                            val currentIndex = bands.indexOf(tariffBand)
                                            if (currentIndex < bands.size - 1) tariffBand =
                                                bands[currentIndex + 1]
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { currentStep = 1 },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Back", color = Color.Black)
                    }
                    Button(
                        onClick = { currentStep = 3 },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827))
                    ) {
                        Text("Continue", color = Color.White)
                    }
                }
            }
            3 -> {
                // Step 3 content
                Text("Step 3: Review Input coming soon", fontFamily = dmSans)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { currentStep = 2 },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827))
                ) {
                    Text("Back", color = Color.White)
                }
            }
        }
    }
}
