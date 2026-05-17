package com.hng14.energyiq.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.EnergyPalette
import com.hng14.energyiq.features.home.presentation.components.HomeTopBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val viewModel = koinViewModel<ProfileViewModel>()
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HomeTopBar(name = state.user?.name)

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EnergyPalette.Amber)
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Error: ${state.error}", color = Color.Red)
                    Button(onClick = { viewModel.loadProfile() }, colors = ButtonDefaults.buttonColors(containerColor = EnergyPalette.Amber)) {
                        Text("Retry")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileHeader(name = state.user?.name ?: "User", email = state.user?.email ?: "")
                
                Spacer(modifier = Modifier.height(32.dp))
                
                ProfileSectionTitle("Account Settings")
                ProfileMenuItem(icon = Icons.Outlined.Person, title = "Personal Information")
                ProfileMenuItem(icon = Icons.Outlined.Lock, title = "Change Password")
                ProfileMenuItem(icon = Icons.Outlined.Notifications, title = "Notification Preferences")
                
                Spacer(modifier = Modifier.height(24.dp))
                
                ProfileSectionTitle("Inverter Details")
                ProfileMenuItem(icon = Icons.Outlined.Bolt, title = "My Inverter Configuration")
                ProfileMenuItem(icon = Icons.Outlined.History, title = "Connection History")
                
                Spacer(modifier = Modifier.height(24.dp))
                
                ProfileSectionTitle("Support & About")
                ProfileMenuItem(icon = Icons.AutoMirrored.Outlined.HelpOutline, title = "Help Center")
                ProfileMenuItem(icon = Icons.Outlined.Info, title = "About EnergyIQ")
                ProfileMenuItem(icon = Icons.Outlined.Description, title = "Terms of Service")
                
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.logout(onLogout) },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF141D2F),
                        contentColor = Color.White
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Logout")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileHeader(name: String, email: String) {
    val initials = name.trim().split(" ").take(2).joinToString("") { it.take(1).uppercase() }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(96.dp),
            shape = CircleShape,
            color = Color(0xFFFFD3A5)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2A2F3C)
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = Color(0xFF111827)
        )
        
        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
private fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF9CA3AF),
            letterSpacing = 1.sp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, title: String) {
    Surface(
        onClick = {},
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF9FAFB)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF111827),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = Color(0xFF111827),
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
