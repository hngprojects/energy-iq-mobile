package com.hng14.energyiq.features.profile.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.hng14.energyiq.core.theme.EnergyPalette
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.features.home.data.BatteryHealthLogEntry
import com.hng14.energyiq.features.home.data.HealthLogRepository
import com.hng14.energyiq.features.home.data.HomeRepository
import com.hng14.energyiq.features.home.data.remote.dto.InverterDto
import com.hng14.energyiq.features.profile.presentation.components.CustomDropdown
import com.hng14.energyiq.*
import org.jetbrains.compose.resources.stringResource
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.flow.Flow
import org.koin.compose.koinInject
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun ReviewLogsScreen(
    userId: String?,
    inverterId: String?,
    onBack: () -> Unit,
) {
    val repo = koinInject<HealthLogRepository>()
    val homeRepository = koinInject<HomeRepository>()
    val dmSans = dmSansFontFamily()

    var inverters by remember { mutableStateOf<List<InverterDto>>(emptyList()) }
    var selectedInverterId by remember { mutableStateOf<String?>(inverterId) }
    var selectedInverterDisplay by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        if (!userId.isNullOrBlank()) {
            inverters = runCatching { homeRepository.getUserInverters() }.getOrDefault(emptyList())
        }
    }

    LaunchedEffect(inverters, selectedInverterId) {
        val inv = inverters.firstOrNull { it.id == selectedInverterId }
        selectedInverterDisplay = inv?.let { inverterDisplay(it) } ?: ""
    }

    val logsFlow: Flow<List<BatteryHealthLogEntry>> = remember(userId, selectedInverterId) {
        if (userId.isNullOrBlank() || selectedInverterId.isNullOrBlank()) flowOf(emptyList())
        else repo.observeBatteryHealthLogs(userId = userId, inverterId = selectedInverterId!!)
    }
    val logs by logsFlow.collectAsState(initial = emptyList())

    var selectedDateText by remember { mutableStateOf("") } // yyyy-mm-dd
    var fromTimeText by remember { mutableStateOf("") } // HH:mm
    var toTimeText by remember { mutableStateOf("") } // HH:mm

    val availableDates = remember(logs) {
        logs.mapNotNull { entry ->
            runCatching {
                Instant.parse(entry.recordedAt)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
                    .toString()
            }.getOrNull()
        }.distinct().sortedDescending()
    }

    val selectedDate: LocalDate? = remember(selectedDateText) {
        selectedDateText.trim().takeIf { it.isNotBlank() }?.let { raw ->
            runCatching { LocalDate.parse(raw) }.getOrNull()
        }
    }
    val fromTime: LocalTime? = remember(fromTimeText) {parseLocalTime(fromTimeText) }
    val toTime: LocalTime? = remember(toTimeText) { parseLocalTime(toTimeText) }

    val filteredLogs = remember(logs, selectedDateText, fromTimeText, toTimeText) {
        logs.filter { entry ->
            val dt = runCatching {
                Instant.parse(entry.recordedAt).toLocalDateTime(TimeZone.currentSystemDefault())
            }.getOrNull() ?: return@filter false

            if (selectedDate != null && dt.date != selectedDate) return@filter false
            if (fromTime != null && dt.time < fromTime) return@filter false
            if (toTime != null && dt.time > toTime) return@filter false
            true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = stringResource(Res.string.common_back),
                    tint = Color(0xFF111827),
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = stringResource(Res.string.logs_battery_health),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                ),
                color = Color(0xFF111827),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            val inverterOptions = remember(inverters) {
                inverters.map { inverterDisplay(it) } + listOf("Other")
            }
            CustomDropdown(
                label = stringResource(Res.string.logs_inverter),
                placeHolder = stringResource(Res.string.logs_select_inverter),
                value = selectedInverterDisplay,
                enabled = !userId.isNullOrBlank() && inverters.isNotEmpty(),
                options = inverterOptions,
                onSelected = { selectedInverterDisplay = it },
                onValueChange = { selectedInverterDisplay = it },
            )
            LaunchedEffect(selectedInverterDisplay, inverters) {
                // Parse back inverterId from the "• <idprefix>" suffix if possible.
                val idPrefix = selectedInverterDisplay.substringAfter("•", "").trim()
                if (idPrefix.isNotBlank()) {
                    val match = inverters.firstOrNull { it.id.startsWith(idPrefix) }
                    if (match != null) selectedInverterId = match.id
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            CustomDropdown(
                label = stringResource(Res.string.common_date),
                placeHolder = stringResource(Res.string.logs_all_dates),
                value = selectedDateText,
                enabled = true,
                options = availableDates + listOf("Other"),
                onSelected = { selectedDateText = it },
                onValueChange = { selectedDateText = it },
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = fromTimeText,
                    onValueChange = { fromTimeText = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text(stringResource(Res.string.logs_from_placeholder)) },
                    shape = RoundedCornerShape(10.dp),
                )
                OutlinedTextField(
                    value = toTimeText,
                    onValueChange = { toTimeText = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text(stringResource(Res.string.logs_to_placeholder)) },
                    shape = RoundedCornerShape(10.dp),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.logs_time_tip),
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = dmSans),
                color = Color(0xFF6B7280),
            )
        }

        if (filteredLogs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                NoLogsCard(
                    message = if (userId.isNullOrBlank() || selectedInverterId.isNullOrBlank()) {
                        stringResource(Res.string.logs_select_inverter_view)
                    } else if (logs.isEmpty()) {
                        stringResource(Res.string.logs_no_logs_yet)
                    } else {
                        stringResource(Res.string.logs_no_match)
                    }
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                items(items = filteredLogs, key = { it.recordedAt + it.status + it.reason }) { entry ->
                    BatteryHealthLogRow(entry = entry)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun BatteryHealthLogRow(entry: BatteryHealthLogEntry) {
    val dmSans = dmSansFontFamily()
    val status = entry.status.uppercase()
    val (chipBg, chipFg) = when (status) {
        "RED" -> Color(0xFFFDEAEA) to EnergyPalette.Danger
        "AMBER" -> Color(0xFFFFF7ED) to EnergyPalette.Amber
        else -> Color(0xFFDDF7E6) to EnergyPalette.BatteryGreen
    }

    val timeText = runCatching {
        val dt = Instant.parse(entry.recordedAt).toLocalDateTime(TimeZone.currentSystemDefault())
        val hh = dt.hour.toString().padStart(2, '0')
        val mm = dt.minute.toString().padStart(2, '0')
        "${dt.date}  $hh:$mm"
    }.getOrDefault(entry.recordedAt)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = chipBg,
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = chipFg,
                    )
                }
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = dmSans),
                    color = Color(0xFF6B7280),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = entry.reason,
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = dmSans),
                color = Color(0xFF111827),
            )
        }
    }
}

private fun parseLocalTime(raw: String): LocalTime? {
    val trimmed = raw.trim()
    if (trimmed.isBlank()) return null
    val parts = trimmed.split(":")
    if (parts.size != 2) return null
    val hh = parts[0].toIntOrNull() ?: return null
    val mm = parts[1].toIntOrNull() ?: return null
    if (hh !in 0..23) return null
    if (mm !in 0..59) return null
    return LocalTime(hour = hh, minute = mm)
}

@Composable
private fun NoLogsCard(message: String) {
    val dmSans = dmSansFontFamily()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFECEEF1)),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = stringResource(Res.string.logs_no_logs),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Bold,
                ),
                color = Color(0xFF111827),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = dmSans),
                color = Color(0xFF6B7280),
            )
        }
    }
}

private fun inverterDisplay(inv: InverterDto): String {
    val label = (inv.model ?: inv.brand).ifBlank { inv.brand }
    val idPrefix = inv.id.take(8)
    return "$label • $idPrefix"
}
