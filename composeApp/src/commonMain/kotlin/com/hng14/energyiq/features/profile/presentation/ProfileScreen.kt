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

import com.hng14.energyiq.*
import org.jetbrains.compose.resources.stringResource

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
    onOpenReports: () -> Unit,
) {
    val viewModel = koinViewModel<ProfileViewModel>()
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val dmSans = dmSansFontFamily()

    val imagePicker = rememberImagePicker(
        onImagePicked = { pickedImage ->
            viewModel.uploadProfilePhoto(
                bytes = pickedImage.bytes,
                fileName = pickedImage.fileName,
                mimeType = pickedImage.mimeType
            )
        },
        onError = { error -> // handle error
        }

    )
    
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
            onUploadPhotoClick = { imagePicker.launch() },
            isUploadingPhoto = state.isUploadingPhoto,
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
                profileUrl = state.user?.profileUrl,
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
                    text = stringResource(Res.string.profile_settings_overview),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(Res.string.profile_overview_desc),
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
                                text = stringResource(Res.string.profile_all_systems_fine),
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
                                text = stringResource(Res.string.profile_2_min_ago),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = dmSans,
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = stringResource(Res.string.common_refresh),
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
                                text = stringResource(Res.string.profile_security_audit),
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
                            text = stringResource(Res.string.profile_security_audit_desc),
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
                                    text = stringResource(Res.string.profile_review_logs),
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
                    title = stringResource(Res.string.profile_account_profile),
                    description = stringResource(Res.string.profile_account_desc),
                    onClick = { route = ProfileRoute.AccountProfile }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsCategoryCard(
                    icon = { Icon(Icons.Outlined.Build, null, tint = Color(0xFF111827), modifier = Modifier.size(22.dp)) },
                    title = stringResource(Res.string.profile_system_device),
                    description = stringResource(Res.string.profile_system_device_desc),
                    onClick = { route = ProfileRoute.SystemDevice }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsCategoryCard(
                    icon = { Icon(Icons.Outlined.Person, null, tint = Color(0xFF111827), modifier = Modifier.size(22.dp)) },
                    title = stringResource(Res.string.profile_team_access),
                    description = stringResource(Res.string.profile_team_desc),
                    onClick = { showComingSoonFeature = "Team & Access" }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsCategoryCard(
                    icon = { Icon(Icons.Outlined.Notifications, null, tint = Color(0xFF111827), modifier = Modifier.size(22.dp)) },
                    title = stringResource(Res.string.common_notifications),
                    description = stringResource(Res.string.profile_notifications_desc),
                    onClick = { showComingSoonFeature = "Notifications" }
                )
                Spacer(modifier = Modifier.height(16.dp))
                SettingsCategoryCard(
                    icon = { TransactionHistoryIcon(modifier = Modifier.size(22.dp), tint = Color(0xFF111827)) },
                    title = stringResource(Res.string.profile_reports),
                    description = stringResource(Res.string.profile_reports_desc),
                    onClick = onOpenReports
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Logout Action
                TextButton(
                    onClick = { viewModel.logout(onLogout) },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = !state.isLoggingOut
                ) {
                    Text(
                        text = stringResource(Res.string.common_sign_out),
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
