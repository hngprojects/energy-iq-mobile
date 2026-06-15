package com.hng14.energyiq.features.costAndSavings.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorStepIndicator(currentStep: Int) {
    val steps = listOf("Select period", "PMS price", "Review input")
    
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, _ ->
                val stepNumber = index + 1
                val isActive = stepNumber == currentStep
                val isCompleted = stepNumber < currentStep

                // Circle
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = when {
                        isCompleted -> Color(0xFFF59E0B)
                        isActive -> Color(0xFF111827)
                        else -> Color.White
                    },
                    border = if (isActive || isCompleted) null else BorderStroke(1.dp, Color(0xFFECEEF1))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = stepNumber.toString(),
                                color = if (isActive) Color.White else Color(0xFFD1D5DB),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Line
                if (index < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(if (isCompleted) Color(0xFFF59E0B) else Color(0xFFECEEF1))
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            steps.forEachIndexed { index, label ->
                val isActive = (index + 1) == currentStep
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) Color(0xFF111827) else Color(0xFF9CA3AF)
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = when(index) {
                        0 -> TextAlign.Start
                        steps.size - 1 -> TextAlign.End
                        else -> TextAlign.Center
                    }
                )
            }
        }
    }
}

@Composable
fun ReviewInputCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    value: String,
    valueSubtitle: String,
    fontFamily: FontFamily,
    isEditing: Boolean = false,
    editContent: @Composable () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFECEEF1)),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF3F4F6)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        icon()
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = Color(0xFF111827)
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = fontFamily,
                            fontSize = 12.sp
                        ),
                        color = Color(0xFF6B7280)
                    )
                }

                if (!isEditing) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = Color(0xFF111827)
                        )
                        Text(
                            text = valueSubtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = fontFamily,
                                fontSize = 12.sp
                            ),
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }
            
            if (isEditing) {
                Spacer(modifier = Modifier.height(16.dp))
                editContent()
            }
        }
    }
}

data class PeriodOption(val title: String, val dateRange: String)

@Composable
fun PeriodSelectionCard(
    option: PeriodOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFFF59E0B) else Color(0xFFECEEF1)),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color(0xFFFEF3C7) else Color(0xFFF3F4F6),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFFF59E0B) else Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFFF59E0B) else Color(0xFFECEEF1)),
                    color = if (isSelected) Color(0xFFF59E0B) else Color.White
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Color(0xFF111827)
                )
                Text(
                    text = option.dateRange,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = fontFamily,
                        fontSize = 13.sp
                    ),
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}
