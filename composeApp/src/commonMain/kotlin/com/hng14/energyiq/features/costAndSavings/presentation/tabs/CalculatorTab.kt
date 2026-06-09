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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.features.costAndSavings.presentation.CostAndSavingsUiState
import com.hng14.energyiq.features.costAndSavings.presentation.components.ReviewInputCard
import com.hng14.energyiq.features.costAndSavings.presentation.components.CalculatorStepIndicator
import com.hng14.energyiq.features.costAndSavings.presentation.components.PeriodOption
import com.hng14.energyiq.features.costAndSavings.presentation.components.PeriodSelectionCard
import com.hng14.energyiq.features.costAndSavings.presentation.components.StepperInput

@Composable
fun CalculatorTab(
    uiState: CostAndSavingsUiState,
    dmSans: FontFamily,
    onDateRangePickRequest: () -> Unit,
    customRangeDescription: String,
    onStepChanged: (Int) -> Unit,
    onPeriodSelected: (String) -> Unit,
    onPriceChanged: (Double) -> Unit,
    onPriceStringChanged: (String) -> Unit,
    onGeneratorTypeChanged: (String) -> Unit,
    onToggleEditing: () -> Unit,
    onContinue: () -> Unit,
    onCalculate: (onSuccess: () -> Unit) -> Unit,
    onNavigateToResults: () -> Unit
) {
    val currentStep = uiState.calculatorStep
    val selectedPeriod = uiState.calculatorSelectedPeriod
    val pmsPrice = uiState.pmsPrice
    val pmsPriceString = uiState.pmsPriceString
    val generatorType = uiState.generatorType
    val isEditing = uiState.isCalculatorStep3Editing

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Text(
            text = "Calculator Savings",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF111827)
        )
        Text(
            text = "Step $currentStep of 3",
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = dmSans,
                color = Color(0xFFF59E0B),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        CalculatorStepIndicator(currentStep = currentStep)

        Spacer(modifier = Modifier.height(40.dp))

        when (currentStep) {
            1 -> {
                Text(
                    text = "Select Calculation Period",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose the time period you want to calculate your savings for.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = dmSans,
                        color = Color(0xFF6B7280),
                        fontSize = 14.sp
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                val periods = listOf(
                    PeriodOption("This Week", "May 31, 2026 - Jun 6, 2026"),
                    PeriodOption("This Month", "Jun 1, 2026 - Jun 30, 2026"),
                    PeriodOption("Last Month", "May 1, 2026 - May 31, 2026"),
                    PeriodOption("Custom Range", customRangeDescription)
                )

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    periods.forEach { period ->
                        PeriodSelectionCard(
                            option = period,
                            isSelected = selectedPeriod == period.title,
                            onClick = {
                                onPeriodSelected(period.title)
                                if (period.title == "Custom Range") onDateRangePickRequest()
                            },
                            fontFamily = dmSans
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Info Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFFBEB),
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = Color(0xFFF59E0B)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Why does the period matter?",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your savings are calculated based on your energy usage, fuel price, and tariff for the selected period",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = dmSans,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                ),
                                color = Color(0xFF6B7280)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            modifier = Modifier.size(width = 56.dp, height = 64.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(20.dp)
                                        .background(
                                            Color(0xFFF59E0B),
                                            RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("JUNE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("6", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp, fontFamily = dmSans), color = Color(0xFF111827))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827), contentColor = Color.White)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text("Continue", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = dmSans, fontWeight = FontWeight.SemiBold, fontSize = 16.sp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }
            }
            2 -> {
                Text(
                    text = "Real-time PMS (Diesel) Price",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = dmSans, fontWeight = FontWeight.Bold, fontSize = 22.sp),
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "We fetch the latest petrol (PMS) price in your area to calculate your generator cost.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = dmSans, color = Color(0xFF9CA3AF), fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, Color(0xFFFEF3C7))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Current PMS Price", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = dmSans, fontWeight = FontWeight.Bold, fontSize = 16.sp), color = Color(0xFF111827))
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("₦${pmsPrice.toInt()}", style = MaterialTheme.typography.headlineLarge.copy(fontFamily = dmSans, fontWeight = FontWeight.Bold, fontSize = 44.sp), color = Color(0xFFF59E0B))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("/ litre", style = MaterialTheme.typography.titleLarge.copy(fontFamily = dmSans, fontWeight = FontWeight.Bold, fontSize = 20.sp), color = Color(0xFF111827))
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFD1FAE5)) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), androidx.compose.foundation.shape.CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Auto-updated", color = Color(0xFF065F46), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Last updated: Today, 4:17 AM", style = MaterialTheme.typography.bodySmall.copy(fontFamily = dmSans, color = Color(0xFF9CA3AF)))
                        Spacer(modifier = Modifier.height(24.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFFEF3C7)))
                        Spacer(modifier = Modifier.height(24.dp))

                        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color.White) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(shape = androidx.compose.foundation.shape.CircleShape, color = Color(0xFFFEF3C7), modifier = Modifier.size(24.dp)) {
                                        Box(contentAlignment = Alignment.Center) { Text("!", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("About this price", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = dmSans, fontWeight = FontWeight.Bold))
                                }
                                listOf("This is the average pump price of PMS (Petrol) in Lagos", "Prices may vary slightly depending on your exact location.", "You can update this price manually if needed").forEach { point ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp).padding(top = 2.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(text = point, style = MaterialTheme.typography.bodySmall.copy(fontFamily = dmSans, color = Color(0xFF6B7280), lineHeight = 18.sp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Adjust Price (Optional)", style = MaterialTheme.typography.titleMedium.copy(fontFamily = dmSans, fontWeight = FontWeight.Bold), color = Color(0xFF111827))
                Text("You can adjust the price if your current local price is different", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = dmSans, color = Color(0xFF9CA3AF), fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                StepperInput(
                    label = "PMS Price (₦/litre)",
                    value = pmsPriceString,
                    onValueChange = onPriceStringChanged,
                    onDecrement = { if (pmsPrice > 0) onPriceChanged(pmsPrice - 10) },
                    onIncrement = { onPriceChanged(pmsPrice + 10) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color(0xFFFFFBEB)) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Tip", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = dmSans, fontWeight = FontWeight.Bold))
                            Text("Use the auto-updated price for the most accurate calculation.", style = MaterialTheme.typography.bodySmall.copy(fontFamily = dmSans, color = Color(0xFF6B7280)))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { onStepChanged(1) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF111827)),
                        border = BorderStroke(1.dp, Color(0xFFECEEF1))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Back", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = dmSans, fontWeight = FontWeight.SemiBold))
                        }
                    }
                    Button(
                        onClick = onContinue,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827), contentColor = Color.White)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Continue", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = dmSans, fontWeight = FontWeight.SemiBold))
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
            3 -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Review Your Input", style = MaterialTheme.typography.titleLarge.copy(fontFamily = dmSans, fontWeight = FontWeight.Bold, fontSize = 22.sp), color = Color(0xFF111827))
                    Surface(onClick = onToggleEditing, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Color(0xFFF59E0B)), color = Color.White) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Edit, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isEditing) "Done editing" else "Edit setup", color = Color(0xFFF59E0B), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = dmSans)
                        }
                    }
                }
                
                Text("Please review the information below. You can edit any input if needed.", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = dmSans, color = Color(0xFF9CA3AF), fontSize = 14.sp))
                Spacer(modifier = Modifier.height(32.dp))

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Calculation Period Card
                    var periodExpanded by remember { mutableStateOf(false) }
                    ReviewInputCard(
                        icon = { Icon(Icons.Outlined.CalendarToday, null, tint = Color(0xFF3B82F6)) },
                        title = "Calculation Period",
                        subtitle = "This time period used for this Calculation",
                        value = if (selectedPeriod == "Custom Range") customRangeDescription else selectedPeriod,
                        valueSubtitle = if (selectedPeriod == "This Week") "May 31, 2026 - Jun 6, 2026" else "",
                        fontFamily = dmSans,
                        isEditing = isEditing,
                        editContent = {
                            Box {
                                Surface(
                                    modifier = Modifier.fillMaxWidth().clickable { periodExpanded = true },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                    color = Color.White
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(
                                            text = if (selectedPeriod == "Custom Range") customRangeDescription else selectedPeriod,
                                            fontFamily = dmSans,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Icon(Icons.Default.KeyboardArrowDown, null, tint = Color(0xFF6B7280))
                                    }
                                }
                                DropdownMenu(
                                    expanded = periodExpanded,
                                    onDismissRequest = { periodExpanded = false },
                                    modifier = Modifier.background(Color.White).fillMaxWidth(0.8f)
                                ) {
                                    listOf("This Week", "This Month", "Last Month", "Custom Range").forEach { period ->
                                        DropdownMenuItem(
                                            text = { Text(period, fontFamily = dmSans) },
                                            onClick = {
                                                if (period == "Custom Range") {
                                                    onDateRangePickRequest()
                                                } else {
                                                    onPeriodSelected(period)
                                                }
                                                periodExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    )

                    // Generator Type Card
                    var genExpanded by remember { mutableStateOf(false) }
                    ReviewInputCard(
                        icon = { Icon(Icons.Outlined.Power, null, tint = Color(0xFF3B82F6)) },
                        title = "Generator Type",
                        subtitle = "Petrol and diesel use different calculations",
                        value = if (generatorType == "PMS") "Petrol (PMS)" else "Diesel",
                        valueSubtitle = "From your savings setup",
                        fontFamily = dmSans,
                        isEditing = isEditing,
                        editContent = {
                            Box {
                                Surface(
                                    modifier = Modifier.fillMaxWidth().clickable { genExpanded = true },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                    color = Color.White
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(if (generatorType == "PMS") "Petrol (PMS)" else "Diesel", fontFamily = dmSans, style = MaterialTheme.typography.bodyMedium)
                                        Icon(Icons.Default.KeyboardArrowDown, null, tint = Color(0xFF6B7280))
                                    }
                                }
                                DropdownMenu(
                                    expanded = genExpanded,
                                    onDismissRequest = { genExpanded = false },
                                    modifier = Modifier.background(Color.White).fillMaxWidth(0.8f)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Petrol (PMS)", fontFamily = dmSans) },
                                        onClick = { onGeneratorTypeChanged("PMS"); genExpanded = false }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Diesel", fontFamily = dmSans) },
                                        onClick = { onGeneratorTypeChanged("Diesel"); genExpanded = false }
                                    )
                                }
                            }
                        }
                    )

                    // Fuel Price Card
                    ReviewInputCard(
                        icon = { Icon(Icons.Outlined.LocalGasStation, null, tint = Color(0xFF8B5CF6)) },
                        title = if (generatorType == "PMS") "Petrol (PMS) Price" else "Diesel Price",
                        subtitle = "Fuel price used for this calculation",
                        value = "₦${pmsPrice.toInt()} / litre",
                        valueSubtitle = "Adjusted for this calculation",
                        fontFamily = dmSans,
                        isEditing = isEditing,
                        editContent = {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                color = Color.White
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("₦", fontFamily = dmSans, color = Color(0xFF111827), fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    BasicTextField(
                                        value = pmsPriceString,
                                        onValueChange = onPriceStringChanged,
                                        modifier = Modifier.weight(1f),
                                        textStyle = TextStyle(fontFamily = dmSans, color = Color(0xFF111827), fontSize = 16.sp),
                                        cursorBrush = SolidColor(Color(0xFF111827)),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true
                                    )
                                    Text("/ litre", fontFamily = dmSans, color = Color(0xFF6B7280), fontSize = 12.sp)
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color(0xFFFFFBEB)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(24.dp), shape = androidx.compose.foundation.shape.CircleShape, color = Color(0xFFF59E0B)) {
                            Box(contentAlignment = Alignment.Center) { Text("!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("These inputs can be updated anytime for more accurate savings calculation.", style = MaterialTheme.typography.bodySmall.copy(fontFamily = dmSans, color = Color(0xFFB45309), fontSize = 12.sp, lineHeight = 16.sp))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { onStepChanged(2) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF111827)),
                        border = BorderStroke(1.dp, Color(0xFFECEEF1))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Back", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = dmSans, fontWeight = FontWeight.SemiBold))
                        }
                    }
                    Button(
                        onClick = { onCalculate { onNavigateToResults() } },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827), contentColor = Color.White)
                    ) {
                        Text("Calculate", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = dmSans, fontWeight = FontWeight.SemiBold))
                    }
                }
            }
        }
    }
}
