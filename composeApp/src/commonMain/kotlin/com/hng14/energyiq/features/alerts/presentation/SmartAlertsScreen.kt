package com.hng14.energyiq.features.alerts.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hng14.energyiq.core.theme.EnergyPalette
import com.hng14.energyiq.core.ui.ComingSoonDialog
import com.hng14.energyiq.core.ui.EmptyStateCard
import com.hng14.energyiq.features.alerts.domain.model.*
import com.hng14.energyiq.features.alerts.presentation.components.*
import com.hng14.energyiq.features.home.presentation.components.HomeTopBar
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartAlertsScreen(
    name: String,
    onInspectAlert: (String) -> Unit,
    onProfileClick: () -> Unit = {}
) {
    val viewModel = koinViewModel<AlertViewModel>()
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    
    var showNotificationsComingSoon by remember { mutableStateOf(false) }

    if (showNotificationsComingSoon) {
        ComingSoonDialog(
            featureName = "Notifications",
            onDismiss = { showNotificationsComingSoon = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        HomeTopBar(
            name = name,
            onNotificationClick = { showNotificationsComingSoon = true },
            onProfileClick = onProfileClick
        )

        if (state.isLoading && state.alerts.isEmpty() && state.stats.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EnergyPalette.Amber)
            }
        } else if (state.errorMessage != null && state.alerts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                AlertErrorCard(
                    message = state.errorMessage!!,
                    onRetry = viewModel::refresh
                )
            }
        } else {
            PullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = viewModel::refresh,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    SmartAlertStatsGrid(stats = state.stats)
                    Spacer(modifier = Modifier.height(40.dp))
                    AlertToolbar(
                        selectedFilter = state.selectedFilter,
                        onFilterSelected = viewModel::onFilterSelected,
                        unresolvedCount = state.summary?.unresolved ?: 0,
                        isRefreshing = state.isLoading,
                        onRefreshClick = viewModel::refresh
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    viewModel.visibleAlerts.forEachIndexed { index, alert ->
                        SmartAlertCard(
                            alert = alert,
                            onInspect = { viewModel.onInspectAlert(alert.id) },
                        )
                        if (index != viewModel.visibleAlerts.lastIndex) {
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    if (viewModel.visibleAlerts.isEmpty() && !state.isLoading) {
                        Spacer(modifier = Modifier.height(24.dp))
                        EmptyStateCard(
                            title = "No Alerts Found",
                            description = "We couldn't find any alerts for this category. Pull down to refresh and try again.",
                            buttonText = "Refresh Now",
                            onRetry = viewModel::refresh
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    state.inspectedAlertId?.let { alertId ->
        viewModel.getDialogContent(alertId)?.let { dialogContent ->
            SmartAlertInspectDialog(
                content = dialogContent,
                isResolving = state.isResolving,
                onDismiss = { viewModel.onInspectAlert(null) },
                onPrimaryAction = {
                    val alert = state.alerts.firstOrNull { it.id == alertId }
                    if (alert != null && !alert.resolved) {
                        viewModel.onResolveAlert(alertId)
                    } else {
                        viewModel.onInspectAlert(null)
                        onInspectAlert(alertId)
                    }
                },
            )
        }
    }
}

@Composable
private fun AlertErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Oops! Something went wrong",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF991B1B)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFB91C1C),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF991B1B)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Text("Retry", color = Color.White)
            }
        }
    }
}

@Composable
private fun SmartAlertStatsGrid(
    stats: List<AlertStat>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        stats.chunked(2).forEach { rowStats ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowStats.forEach { stat ->
                    SmartAlertStatCard(
                        stat = stat,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
