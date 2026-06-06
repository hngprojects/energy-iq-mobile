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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
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
import com.hng14.energyiq.features.costAndSavings.presentation.tabs.CalculatorTab
import com.hng14.energyiq.features.costAndSavings.presentation.tabs.CumulativeTrackerTab
import com.hng14.energyiq.features.costAndSavings.presentation.tabs.ResultsTab
import com.hng14.energyiq.features.costAndSavings.presentation.tabs.SummaryTab
import com.hng14.energyiq.features.home.presentation.components.BackHomeTopBar
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.collectAsState
import com.hng14.energyiq.Res
import com.hng14.energyiq.cas_greeting_subtitle
import com.hng14.energyiq.cas_period_custom_desc
import com.hng14.energyiq.cas_tab_calculator
import com.hng14.energyiq.cas_tab_cumulative
import com.hng14.energyiq.cas_tab_results
import com.hng14.energyiq.cas_tab_summary
import com.hng14.energyiq.cas_title
import com.hng14.energyiq.common_cancel
import com.hng14.energyiq.common_confirm
import org.jetbrains.compose.resources.stringResource

private fun formatMillis(millis: Long?): String {
    if (millis == null) return ""
    val instant = Instant.fromEpochMilliseconds(millis)
    val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    val monthName = date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    return "$monthName ${date.dayOfMonth}, ${date.year}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CostAndSavingsScreen(
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<CostAndSavingsViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    
    val authRepository = koinInject<AuthRepository>()
    var userName by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        userName = authRepository.getCurrentUser()?.name
    }

    val scrollState = rememberScrollState()
    val dmSans = dmSansFontFamily()

    // Tab State
    val tabs = listOf(
        stringResource(Res.string.cas_tab_summary),
        stringResource(Res.string.cas_tab_calculator),
        stringResource(Res.string.cas_tab_results),
        stringResource(Res.string.cas_tab_cumulative)
    )
    var selectedTab by remember { mutableStateOf(tabs[0]) }

    // Custom Date Range Picker State
    val defaultCustomDesc = stringResource(Res.string.cas_period_custom_desc)
    var customRangeDescription by remember(defaultCustomDesc) { mutableStateOf(defaultCustomDesc) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    val dateRangePickerState = rememberDateRangePickerState()

    if (showDateRangePicker) {
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = dateRangePickerState.selectedStartDateMillis
                        val end = dateRangePickerState.selectedEndDateMillis
                        if (start != null && end != null) {
                            customRangeDescription = "${formatMillis(start)} - ${formatMillis(end)}"
                        }
                        showDateRangePicker = false
                    }
                ) {
                    Text(stringResource(Res.string.common_confirm), color = Color(0xFFF59E0B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) {
                    Text(stringResource(Res.string.common_cancel))
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f)
            )
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = Color.White,
        topBar = {
            BackHomeTopBar(
                title = stringResource(Res.string.cas_title),
                name = userName,
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    GreetingHeader(
                        name = userName,
                        subtitle = stringResource(Res.string.cas_greeting_subtitle)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timeframe Filter
                    var expanded by remember { mutableStateOf(false) }
                    val timeframes = listOf("Daily", "Weekly", "Monthly")

                    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
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
                                    tint = Color(0xFF6B7280)
                                )
                                Text(
                                    text = uiState.selectedTimeframe,
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
                                        viewModel.onTimeframeSelected(timeframe)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tab Bar
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(tabs) { tab ->
                            val isSelected = tab == selectedTab
                            Column(
                                modifier = Modifier.clickable { selectedTab = tab },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = tab,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = dmSans,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 16.sp
                                    ),
                                    color = if (isSelected) Color(0xFF111827) else Color(0xFF6B7280),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(2.dp)
                                            .background(Color(0xFFF59E0B)) // Primary color indicator
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp)
                ) {
                    when (selectedTab) {
                        tabs[0] -> {
                            SummaryTab(
                                uiState = uiState,
                                onCheckCalculator = { selectedTab = tabs[1] },
                                dmSans = dmSans
                            )
                        }

                        tabs[1] -> {
                            CalculatorTab(
                                dmSans = dmSans,
                                onDateRangePickRequest = { showDateRangePicker = true },
                                customRangeDescription = customRangeDescription
                            )
                        }

                        tabs[2] -> {
                            ResultsTab(dmSans = dmSans)
                        }

                        tabs[3] -> {
                            CumulativeTrackerTab(dmSans = dmSans)
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }

            if (uiState.isLoading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White.copy(alpha = 0.7f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = Color(0xFFF59E0B)
                        )
                    }
                }
            }
        }
    }
}
