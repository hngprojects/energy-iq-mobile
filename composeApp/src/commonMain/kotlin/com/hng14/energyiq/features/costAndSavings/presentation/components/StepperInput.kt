package com.hng14.energyiq.features.costAndSavings.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.dmSansFontFamily

@Composable
fun StepperInput(
    label: String,
    value: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dmSans = dmSansFontFamily()
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
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
                    text = value,
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
                        onClick = onDecrement
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    StepperButton(
                        icon = { Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(20.dp)) },
                        onClick = onIncrement
                    )
                }
            }
        }
    }
}

@Composable
fun StepperButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = RoundedCornerShape(6.dp),
        color = Color.White,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}
