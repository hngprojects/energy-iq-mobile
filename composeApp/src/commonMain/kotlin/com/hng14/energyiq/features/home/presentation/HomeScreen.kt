package com.hng14.energyiq.features.home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.EnergyPalette
import com.hng14.energyiq.core.ui.DangerVectorIcon
import com.hng14.energyiq.core.ui.TransactionHistoryIcon
import com.hng14.energyiq.features.alerts.presentation.SmartAlertsScreen
import com.hng14.energyiq.features.profile.presentation.ProfileScreen
import com.hng14.energyiq.features.reports.presentation.ReportsScreen
import com.hng14.energyiq.features.home.presentation.components.BatteryCard
import com.hng14.energyiq.features.home.presentation.components.DraggableFab
import com.hng14.energyiq.features.home.presentation.components.EnergyUsageCard
import org.koin.compose.viewmodel.koinViewModel
import com.hng14.energyiq.features.home.presentation.components.HomeTopBar
import com.hng14.energyiq.features.home.presentation.components.MetricCard
import com.hng14.energyiq.features.home.presentation.components.PowerUsageCard
import com.hng14.energyiq.features.home.presentation.components.SavingsOverviewCard
import com.hng14.energyiq.features.home.presentation.components.WarningBanner

private enum class HomeTab(val title: String, val icon: ImageVector?) {
    Dashboard("Dashboard", Icons.Outlined.GridView),
    Alert("Alert", null),
    Reports("Reports", null),
    Profile("Profile", Icons.Outlined.Person),
}

@Composable
fun HomeScreen(
    onOpenChat: () -> Unit,
    onLogout: () -> Unit,
) {
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(HomeTab.Dashboard) }

    val name = state.user?.name ?: ""
    val fullName = name.trim()
    val firstName = fullName.substringBefore(" ", fullName)
    val displayName = firstName.ifBlank { "User" }
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            HomeBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                HomeTab.Dashboard -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    HomeTopBar(name = name)
                    if (state.isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = EnergyPalette.Amber)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                        ) {
                            DashboardContent(displayName = displayName)
                        }
                    }
                }

                HomeTab.Alert -> Box(modifier = Modifier.padding(paddingValues)) {
                    SmartAlertsScreen(name = name, onInspectAlert = {})
                }

                HomeTab.Profile -> Box(modifier = Modifier.padding(paddingValues)) {
                    ProfileScreen(onLogout = onLogout)
                }

                HomeTab.Reports -> Box(modifier = Modifier.padding(paddingValues)) {
                    ReportsScreen(
                        name = name,
                        onViewReport = {},
                        onDownloadReport = {},
                    )
                }

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    HomeTopBar(name = name)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${selectedTab.title} Screen",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            if (selectedTab == HomeTab.Dashboard) {
                DraggableFab(
                    onClick = onOpenChat,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 104.dp), // Increased padding to clear navbar
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(displayName: String) {
    Column {
        Text(
            text = "Good afternoon, $displayName",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
            ),
            color = Color(0xFF111827),
        )
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Your system has been running\non solar for 6 hrs today.",
            style = MaterialTheme.typography.bodySmall.copy(
                lineHeight = 16.sp,
            ),
            color = Color(0xFF6B7280),
            modifier = Modifier.padding(top = 4.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))
        WarningBanner()
        Spacer(modifier = Modifier.height(20.dp))
        MetricCard(
            title = "Solar Input",
            value = "4.2 kW",
            subtitle = "Peak was 5.8 kW at 03:30 pm",
            showSunIcon = true,
            badgeText = "Panel working well",
            badgeColor = Color(0xFFDDF7E6),
            badgeContentColor = EnergyPalette.BatteryGreen,
        )
        Spacer(modifier = Modifier.height(30.dp))
        BatteryCard()
        Spacer(modifier = Modifier.height(30.dp))
        MetricCard(
            title = "Running now",
            value = "2.8 kW",
            subtitle = "Steady load",
            showRunningLowIcon = true,
            wrapSubtitleInContainer = true,
        )
        Spacer(modifier = Modifier.height(30.dp))
        PowerUsageCard()
        Spacer(modifier = Modifier.height(30.dp))
        SavingsOverviewCard()
        Spacer(modifier = Modifier.height(30.dp))
        EnergyUsageCard()
        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
private fun HomeBottomNavigation(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.height(92.dp).padding(top = 12.dp)
    ) {
        HomeTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            val tint = if (isSelected) EnergyPalette.Amber else Color(0xFF2A2F3C)
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    when (tab) {
                        HomeTab.Alert -> {
                            DangerVectorIcon(
                                modifier = Modifier.size(22.dp),
                                contentDescription = tab.title,
                                tint = tint
                            )
                        }

                        HomeTab.Reports -> {
                            TransactionHistoryIcon(
                                modifier = Modifier.size(22.dp),
                                contentDescription = tab.title,
                                tint = tint
                            )
                        }

                        else -> {
                            androidx.compose.material3.Icon(
                                imageVector = tab.icon!!,
                                contentDescription = tab.title,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EnergyPalette.Amber,
                    selectedTextColor = EnergyPalette.Amber,
                    unselectedIconColor = Color(0xFF2A2F3C),
                    unselectedTextColor = Color(0xFF2A2F3C),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

