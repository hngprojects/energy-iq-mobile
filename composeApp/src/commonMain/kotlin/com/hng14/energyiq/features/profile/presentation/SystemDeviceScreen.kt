package com.hng14.energyiq.features.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.ui.BellIcon
import com.hng14.energyiq.features.auth.domain.model.User
import com.hng14.energyiq.features.home.data.HomeRepository
import com.hng14.energyiq.features.home.data.remote.dto.InverterDto
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun SystemDeviceScreen(
    user: User?,
    onBack: () -> Unit,
    onConnectNewInverter: () -> Unit,
    onNotifications: () -> Unit,
) {
    val dmSans = dmSansFontFamily()
    val scrollState = rememberScrollState()
    val homeRepository = koinInject<HomeRepository>()
    val scope = rememberCoroutineScope()

    var inverters by remember { mutableStateOf<List<InverterDto>>(emptyList()) }
    var selectedInverterId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        // Always prefer persisted selection so leaving/returning restores correctly.
        selectedInverterId = runCatching { homeRepository.getSelectedInverterId() }.getOrNull()
            ?: homeRepository.peekSessionInverterId()
        inverters = runCatching { homeRepository.getUserInverters() }.getOrDefault(emptyList())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        TopBar(
            name = user?.name,
            onBack = onBack,
            onNotifications = onNotifications,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(
                text = "System & Device Management",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                ),
                color = Color(0xFF111827),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Overview of your connected systems, inverter configurations, and diagnostic alerts.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = dmSans,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                ),
                color = Color(0xFF6B7280),
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onConnectNewInverter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF141D2F),
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = "+  Connect New Inverter",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            inverters.forEach { inv ->
                val lastSync = inv.lastSyncedAt?.let { ts ->
                    formatDateTime(ts)
                } ?: "--"

                val connectionDate = inv.createdAt?.let { ts ->
                    runCatching {
                        val dt = Instant.parse(ts).toLocalDateTime(TimeZone.currentSystemDefault())
                        dt.date.toString()
                    }.getOrDefault("--")
                } ?: "--"

                InverterCard(
                    inverterBrand = inv.brand,
                    inverterModel = inv.model ?: "Hybrid Inverter",
                    isActive = inv.isActive && (inv.isOffline != true),
                    isReadingNow = selectedInverterId != null && inv.id == selectedInverterId,
                    serialNumber = inv.serialNumber ?: inv.id,
                    connectionDate = connectionDate,
                    lastSync = lastSync,
                    onReconnect = {
                        // Switch which inverter the dashboard reads from, then immediately fetch its metrics.
                        scope.launch {
                            runCatching { homeRepository.setSelectedInverterId(inv.id) }
                            selectedInverterId = inv.id
                            runCatching { homeRepository.getInverterDashboard() }
                        }
                    },
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun TopBar(
    name: String?,
    onBack: () -> Unit,
    onNotifications: () -> Unit,
) {
    val initials = name
        ?.trim()
        ?.split(Regex("\\s+"))
        ?.filter { it.isNotBlank() }
        ?.take(2)
        ?.joinToString("") { it.first().uppercase() }
        ?.ifBlank { "U" }
        ?: "U"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF111827),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            BellIcon(
                contentDescription = "Notifications",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onNotifications() },
            )
            Spacer(modifier = Modifier.size(12.dp))
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = Color(0xFFFFD3A5),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF2A2F3C),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun InverterCard(
    inverterBrand: String,
    inverterModel: String,
    isActive: Boolean,
    isReadingNow: Boolean,
    serialNumber: String,
    connectionDate: String,
    lastSync: String,
    onReconnect: () -> Unit,
) {
    val dmSans = dmSansFontFamily()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECEEF1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = true),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF111827),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = inverterBrand.take(2).uppercase(),
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.Bold,
                                ),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    Column {
                        Text(
                            text = inverterBrand,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = dmSans,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = Color(0xFF111827),
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = inverterModel,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = dmSans),
                            color = Color(0xFF6B7280),
                            maxLines = 1,
                        )
                    }
                }

                Spacer(modifier = Modifier.size(12.dp))

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (isActive) Color(0xFFDDF7E6) else Color(0xFFFDEAEA),
                    modifier = Modifier
                        .height(28.dp),
                ) {
                    Text(
                        text = if (isActive) "Active" else "Offline",
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .defaultMinSize(minWidth = 64.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = if (isActive) Color(0xFF16A34A) else Color(0xFFDC2626),
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            DetailRow(label = "Serial Number", value = serialNumber)
            Spacer(modifier = Modifier.height(10.dp))
            DetailRow(label = "Connection Date", value = connectionDate)
            Spacer(modifier = Modifier.height(10.dp))
            DetailRow(label = "Last Sync", value = lastSync)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = onReconnect,
                    modifier = Modifier.weight(1f),
                    enabled = !isReadingNow,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF9FAFB),
                        contentColor = Color(0xFF111827),
                    ),
                    border = BorderStroke(1.dp, Color(0xFFECEEF1)),
                ) {
                    Text(
                        text = if (isReadingNow) "Reading" else "Reconnect",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }

                // View Details (coming soon)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    val dmSans = dmSansFontFamily()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = dmSans),
            color = Color(0xFF6B7280),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.SemiBold,
            ),
            color = Color(0xFF111827),
        )
    }
}

private fun formatDateTime(iso: String): String {
    return runCatching {
        val dt = Instant.parse(iso).toLocalDateTime(TimeZone.currentSystemDefault())
        val hh = dt.hour.toString().padStart(2, '0')
        val mm = dt.minute.toString().padStart(2, '0')
        "${dt.date} $hh:$mm"
    }.getOrDefault(iso)
}
