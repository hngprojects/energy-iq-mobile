package com.hng14.energyiq.features.alerts.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hng14.energyiq.core.ui.ServerErrorDialog
import com.hng14.energyiq.features.alerts.domain.model.*
import com.hng14.energyiq.features.alerts.presentation.components.*
import com.hng14.energyiq.features.home.presentation.components.HomeTopBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SmartAlertsScreen(
    name: String,
    onInspectAlert: (String) -> Unit,
) {
    val viewModel = koinViewModel<AlertViewModel>()
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        HomeTopBar(name = name)
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
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (state.isLoading) {
                androidx.compose.material3.CircularProgressIndicator(color = Color(0xFF141D2F))
                Spacer(modifier = Modifier.height(16.dp))
            }
            viewModel.visibleAlerts.forEachIndexed { index, alert ->
                SmartAlertCard(
                    alert = alert,
                    onInspect = { viewModel.onInspectAlert(alert.id) },
                )
                if (index != viewModel.visibleAlerts.lastIndex) {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    state.errorMessage?.let { message ->
        ServerErrorDialog(
            message = message,
            onDismiss = viewModel::onErrorDismissed,
        )
    }

    state.inspectedAlertId?.let { alertId ->
        viewModel.getDialogContent(alertId)?.let { dialogContent ->
            SmartAlertInspectDialog(
                content = dialogContent,
                onDismiss = { viewModel.onInspectAlert(null) },
                onPrimaryAction = {
                    viewModel.onInspectAlert(null)
                    onInspectAlert(alertId)
                },
            )
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
