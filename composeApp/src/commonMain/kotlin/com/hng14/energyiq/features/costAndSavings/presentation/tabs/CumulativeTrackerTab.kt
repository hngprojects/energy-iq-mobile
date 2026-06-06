package com.hng14.energyiq.features.costAndSavings.presentation.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun CumulativeTrackerTab(dmSans: FontFamily) {
    Box(
        modifier = Modifier.fillMaxSize().padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Cumulative tracker coming soon",
            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = dmSans),
            color = Color.Gray
        )
    }
}
