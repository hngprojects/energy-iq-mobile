package com.hng14.energyiq.features.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.EnergyPalette
import com.hng14.energyiq.core.ui.ComingSoonDialog
import com.hng14.energyiq.core.ui.DangerVectorIcon
import com.hng14.energyiq.core.ui.EmptyStateCard
import com.hng14.energyiq.core.ui.TransactionHistoryIcon
import com.hng14.energyiq.features.alerts.presentation.SmartAlertsScreen
import com.hng14.energyiq.features.home.data.remote.dto.InverterDashboardData
import com.hng14.energyiq.features.home.presentation.components.BatteryCard
import com.hng14.energyiq.features.home.presentation.components.DraggableFab
import com.hng14.energyiq.features.home.presentation.components.EnergyUsageCard
import com.hng14.energyiq.features.home.presentation.components.HomeTopBar
import com.hng14.energyiq.features.home.presentation.components.MetricCard
import com.hng14.energyiq.features.home.presentation.components.SavingsOverviewCard
import com.hng14.energyiq.features.home.presentation.components.WarningBanner
import com.hng14.energyiq.features.costAndSavings.presentation.CostAndSavingsScreen
import com.hng14.energyiq.features.profile.presentation.ProfileScreen
import com.hng14.energyiq.core.ui.CasIcon
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import org.koin.compose.viewmodel.koinViewModel

private enum class HomeTab(val title: String, val icon: ImageVector?) {
    Dashboard("Dashboard", Icons.Outlined.GridView),
    Alert("Alert", null),
    CostAndSavings("Savings", null),
    Profile("Profile", Icons.Outlined.Person),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    startOnProfile: Boolean = false,
    onOpenChat: () -> Unit,
    onLogout: () -> Unit,
    onOpenInverterSetup: () -> Unit,
    onOpenReports: () -> Unit,
) {
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(if (startOnProfile) HomeTab.Profile else HomeTab.Dashboard) }
    var showNotificationsComingSoon by remember { mutableStateOf(false) }

    val name = state.user?.name ?: ""
    val fullName = name.trim()
    val firstName = fullName.substringBefore(" ", fullName)
    val displayName = firstName.ifBlank { "User" }
    val scrollState = rememberScrollState()

    // Ensure the dashboard fetch runs when the Home screen becomes visible.
    // In some navigation/backstack flows the ViewModel instance can be retained
    // across auth transitions; relying only on init{} can skip the first fetch
    // after login.
    LaunchedEffect(selectedTab) {
        if (selectedTab == HomeTab.Dashboard || selectedTab == HomeTab.Alert || selectedTab == HomeTab.CostAndSavings) {
            viewModel.refresh()
        }

    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            HomeBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                }
            )
        }
    ) { paddingValues ->
        if (showNotificationsComingSoon) {
            ComingSoonDialog(
                featureName = "Notifications",
                onDismiss = { showNotificationsComingSoon = false }
            )
        }
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                HomeTab.Dashboard -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    HomeTopBar(
                        name = name,
                        profileUrl = state.user?.profileUrl,
                        onNotificationClick = { showNotificationsComingSoon = true },
                        onProfileClick = { selectedTab = HomeTab.Profile }
                    )
                    if (state.isLoading && (state.dashboardData == null || state.dashboardData?.currentReadings == null) && !state.isInitialSync) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = EnergyPalette.Amber)
                        }
                    } else if (state.isInitialSync && state.dashboardData == null) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            InitialSyncCard(
                                message = state.errorMessage ?: "Connecting to your inverter. This usually takes a few moments...",
                                onRetry = viewModel::refresh
                            )
                        }
                    } else if (state.errorMessage != null && state.dashboardData == null) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            EmptyStateCard(
                                title = "Connection Error",
                                description = state.errorMessage!!,
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
                                DashboardContent(
                                    displayName = displayName,
                                    data = state.dashboardData,
                                    showHealthBanner = state.dashboardData?.health?.status != "GREEN" && !state.isHealthBannerDismissed,
                                    onDismissHealth = viewModel::onDismissHealthBanner,
                                    onNavigateToSavings = { selectedTab = HomeTab.CostAndSavings }
                                )
                            }
                        }
                    }
                }

                HomeTab.Alert -> Box(modifier = Modifier.padding(paddingValues)) {
                    SmartAlertsScreen(
                        name = name, 
                        profileUrl = state.user?.profileUrl,
                        onInspectAlert = {},
                        onProfileClick = { selectedTab = HomeTab.Profile }
                    )
                }

                HomeTab.Profile -> Box(modifier = Modifier.padding(paddingValues)) {
                    ProfileScreen(
                        onLogout = onLogout,
                        onOpenInverterSetup = onOpenInverterSetup,
                        onOpenReports = onOpenReports,
                    )
                }

                HomeTab.CostAndSavings -> Box(modifier = Modifier.padding(paddingValues)) {
                    CostAndSavingsScreen(
                        userName = state.user?.name,
                        profileUrl = state.user?.profileUrl,
                        onProfileClick = { selectedTab = HomeTab.Profile },
                        onBack = null
                    )
                }

            }

            if (selectedTab == HomeTab.Dashboard) {
                DraggableFab(
                    onClick = onOpenChat,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        // Keep the FAB comfortably above the bottom nav bar (which also applies navigationBarsPadding).
                        .padding(end = 20.dp, bottom = 140.dp),
                )
            }
        }
    }
}

@Composable
private fun InitialSyncCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFECEEF1))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(64.dp).background(Color(0xFFF9FAFB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color(0xFF141D2F),
                    strokeWidth = 3.dp
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Setting up your Dashboard",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF111827),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF141D2F)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Retry Connection", color = Color.White)
            }
        }
    }
}

@Composable
private fun DashboardContent(
    displayName: String,
    data: InverterDashboardData?,
    showHealthBanner: Boolean,
    onDismissHealth: () -> Unit,
    onNavigateToSavings: () -> Unit,
) {
    val greeting = run {
        val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
        when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    Column {
        Text(
            text = "$greeting, $displayName",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
            ),
            color = Color(0xFF111827),
        )
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (data?.systemOffline == true) "Your system is currently offline." else "Your system is running normally.",
            style = MaterialTheme.typography.bodySmall.copy(
                lineHeight = 16.sp,
            ),
            color = Color(0xFF6B7280),
            modifier = Modifier.padding(top = 4.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))
        if (showHealthBanner && data?.health != null) {
            WarningBanner(
                reason = data.health.reason,
                onDismiss = onDismissHealth
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        
        MetricCard(
            title = "Solar Input",
            value = "${data?.currentReadings?.solarKw ?: 0.0} kW",
            subtitle = "Live solar generation",
            showSunIcon = true,
            badgeText = data?.health?.status ?: "Healthy",
            badgeColor = when(data?.health?.status) {
                "RED" -> Color(0xFFFDEAEA)
                "AMBER" -> Color(0xFFFFF7ED)
                else -> Color(0xFFDDF7E6)
            },
            badgeContentColor = when(data?.health?.status) {
                "RED" -> EnergyPalette.Danger
                "AMBER" -> EnergyPalette.Amber
                else -> EnergyPalette.BatteryGreen
            },
        )
        Spacer(modifier = Modifier.height(30.dp))
        BatteryCard(
            soc = data?.currentReadings?.batterySocPercent ?: 0.0,
            subtitle = data?.health?.reason ?: "System healthy"
        )
        Spacer(modifier = Modifier.height(30.dp))
        MetricCard(
            title = "Running now",
            value = "${data?.currentReadings?.loadKw ?: 0.0} kW",
            subtitle = "Active power load",
            showRunningLowIcon = true,
            wrapSubtitleInContainer = true,
        )
        Spacer(modifier = Modifier.height(30.dp))
        // PowerUsageCard() // Temporarily commented out as API does not provide breakdown data yet
        // Spacer(modifier = Modifier.height(30.dp))
        SavingsOverviewCard(
            savedToday = data?.nairaSavedToday ?: 0.0,
            savedMonth = data?.nairaSavedThisMonth ?: 0.0,
            onClick = onNavigateToSavings
        )
        Spacer(modifier = Modifier.height(30.dp))
        EnergyUsageCard(history = data?.sevenDayHistory ?: emptyList())
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
        modifier = Modifier.navigationBarsPadding(),
        windowInsets = NavigationBarDefaults.windowInsets
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

                        HomeTab.CostAndSavings -> {
                            CasIcon(
                                modifier = Modifier.size(22.dp),
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

