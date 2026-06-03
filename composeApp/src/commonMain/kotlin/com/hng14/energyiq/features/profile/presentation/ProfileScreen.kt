package com.hng14.energyiq.features.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.ui.*
import com.hng14.energyiq.features.home.presentation.components.HomeTopBar
import com.hng14.energyiq.features.home.data.HomeRepository
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private enum class ProfileRoute {
    Overview,
    AccountProfile,
    ReviewLogs,
    SystemDevice,
}

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onOpenInverterSetup: () -> Unit,
    onOpenCostAndSavings: () -> Unit,
) {
    val viewModel = koinViewModel<ProfileViewModel>()
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val dmSans = dmSansFontFamily()
    
    var showComingSoonFeature by remember { mutableStateOf<String?>(null) }
    var route by remember { mutableStateOf(ProfileRoute.Overview) }

    if (showComingSoonFeature != null) {
        ComingSoonDialog(
            featureName = showComingSoonFeature!!,
            onDismiss = { showComingSoonFeature = null }
        )
    }

    if (route == ProfileRoute.AccountProfile) {
        state.successMessage?.let { message ->
            SuccessDialog(
                message = message,
                onDone = viewModel::onDismissSuccess,
            )
        }
        state.error?.let { message ->
            ServerErrorDialog(
                message = message,
                onDismiss = viewModel::onDismissError,
            )
        }
        AccountProfileScreen(
            user = state.user,
            onBack = { route = ProfileRoute.Overview },
            onUploadPhotoClick = { showComingSoonFeature = "Upload photo" },
            isSaving = state.isSaving,
            onSaveChanges = { fullName, businessName, businessType, userState, userCity, aiLanguage ->
                viewModel.savePersonalSettings(
                    fullName = fullName,
                    businessName = businessName,
                    businessType = businessType,
                    state = userState,
                    city = userCity,
                    aiLanguage = aiLanguage,
                    profileUrl = state.uploadedProfileUrl ?: state.user?.profileUrl,
                )
            },
        )
        return
    }

    if (route == ProfileRoute.ReviewLogs) {
        val homeRepository = koinInject<HomeRepository>()
        ReviewLogsScreen(
            userId = state.user?.id,
            inverterId = homeRepository.peekSessionInverterId(),
            onBack = { route = ProfileRoute.Overview },
        )
        return
    }

    if (route == ProfileRoute.SystemDevice) {
        SystemDeviceScreen(
            user = state.user,
            onBack = { route = ProfileRoute.Overview },
            onConnectNewInverter = onOpenInverterSetup,
            onNotifications = { showComingSoonFeature = "Notifications" },
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            HomeTopBar(
                name = state.user?.name,
                onNotificationClick = { showComingSoonFeature = "Notifications" },
                onProfileClick = { /* Already on profile tab */ }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Text(
                    text = "Settings Overview",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Manage your EnergyIQ environment, team permissions and system health",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = dmSans,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Status Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFECEEF1))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF22C55E), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "All systems is working fine",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF22C55E),
                                    fontSize = 13.sp
                                )
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "2 min ago",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = dmSans,
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = "Refresh",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Security Audit Card
                InsightOutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    paddingValues = PaddingValues(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF3F4F6)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFF111827),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = "Security Audit",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = Color(0xFF111827)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Last audit completed 2 hours ago. No anomalies detected.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = dmSans,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { showComingSoonFeature = "Review Logs" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF141D2F),
                                contentColor = Color.White
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Review Logs",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = dmSans,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Large Category Cards
                SettingsCategoryCard(
                    icon = { Icon(Icons.Outlined.Person, null, tint = Color(0xFF111827), modifier = Modifier.size(22.dp)) },
                    title = "Account & Profile",
                    description = "Manage your enterprise identity, update localisation preference, and configure multi-factor authentication protocols.",
                    onClick = { route = ProfileRoute.AccountProfile }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsCategoryCard(
                    icon = { Icon(Icons.Outlined.Build, null, tint = Color(0xFF111827), modifier = Modifier.size(22.dp)) },
                    title = "System & Device",
                    description = "Manage your enterprise identity, update localisation preference, and configure multi-factor authentication protocols.",
                    onClick = { route = ProfileRoute.SystemDevice }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsCategoryCard(
                    icon = { Icon(Icons.Outlined.Person, null, tint = Color(0xFF111827), modifier = Modifier.size(22.dp)) },
                    title = "Team & Access",
                    description = "Control organisational hierarchies by assigning specific user roles, permissions, and administrative access levels.",
                    onClick = { showComingSoonFeature = "Team & Access" }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsCategoryCard(
                    icon = { Icon(Icons.Outlined.Notifications, null, tint = Color(0xFF111827), modifier = Modifier.size(22.dp)) },
                    title = "Notifications",
                    description = "Control organisational hierarchies by assigning specific user roles, permissions, and administrative access levels.",
                    onClick = { showComingSoonFeature = "Notifications" }
                )
                Spacer(modifier = Modifier.height(16.dp))
                SettingsCategoryCard(
                    icon = { CasIcon(modifier = Modifier.size(22.dp), tint = Color(0xFF111827)) },
                    title = "Cost And Savings",
                    description = "Track your energy ROI, avoided generator costs, and calculate future solar savings.",
                    onClick = onOpenCostAndSavings
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Logout Action
                TextButton(
                    onClick = { viewModel.logout(onLogout) },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = !state.isLoggingOut
                ) {
                    Text(
                        text = "Sign Out",
                        color = Color(0xFFDC2626),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(44.dp))
            }
        }
    }
}

@Composable
private fun SettingsCategoryCard(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    val dmSans = dmSansFontFamily()
    InsightOutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        paddingValues = PaddingValues(24.dp)
    ) {
        Column {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF3F4F6)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    icon()
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color(0xFF111827)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = dmSans,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                ),
                color = Color(0xFF6B7280)
            )
        }
    }
}
