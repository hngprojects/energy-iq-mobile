package com.hng14.energyiq.features.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.hng14.energyiq.core.data.NigeriaStateCities
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.features.auth.domain.model.User
import com.hng14.energyiq.features.profile.presentation.components.CustomDropdown
import com.hng14.energyiq.features.profile.presentation.data.local.businessTypeOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AccountProfileScreen(
    user: User?,
    onBack: () -> Unit,
    onUploadPhotoClick: () -> Unit,
    isSaving: Boolean,
    onSaveChanges: (
        fullName: String,
        businessName: String,
        businessType: String,
        state: String,
        city: String,
        aiLanguage: String,
    ) -> Unit,
) {
    val dmSans = dmSansFontFamily()
    val scrollState = rememberScrollState()

    val initials = user?.name
        ?.trim()
        ?.split(Regex("\\s+"))
        ?.filter { it.isNotBlank() }
        ?.take(2)
        ?.joinToString("") { it.first().uppercase() }
        ?.ifBlank { "U" }
        ?: "U"

    var isEditing by remember { mutableStateOf(false) }
    var fullName by remember(user?.name) { mutableStateOf(user?.name.orEmpty()) }
    var email by remember(user?.email) { mutableStateOf(user?.email.orEmpty()) }
    var businessName by remember(user?.businessName) { mutableStateOf(user?.businessName.orEmpty()) }
    var businessType by remember(user?.businessType) { mutableStateOf(user?.businessType.orEmpty()) }
    var state by remember(user?.state) { mutableStateOf(user?.state.orEmpty()) }
    var city by remember(user?.city) { mutableStateOf(user?.city.orEmpty()) }
    var aiLanguage by remember(user?.aiLanguage) { mutableStateOf(user?.aiLanguage?.ifBlank { "English" } ?: "English") }
    var uploadError by remember { mutableStateOf<String?>(null) }

    val stateOptions = remember { NigeriaStateCities.states + "Other" }
    val aiLanguageOptions = remember { listOf("English", "Pidgin") }

    // Snapshots for Cancel.
    var snapshotFullName by remember { mutableStateOf(fullName) }
    var snapshotBusinessName by remember { mutableStateOf(businessName) }
    var snapshotBusinessType by remember { mutableStateOf(businessType) }
    var snapshotState by remember { mutableStateOf(state) }
    var snapshotCity by remember { mutableStateOf(city) }
    var snapshotAiLanguage by remember { mutableStateOf(aiLanguage) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF111827),
                    )
                }
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "Profile Settings",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    ),
                    color = Color(0xFF111827),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Text(
                text = "Manage your personal and business information.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = dmSans,
                    fontSize = 13.sp,
                ),
                color = Color(0xFF6B7280),
            )

            Spacer(modifier = Modifier.height(14.dp))

            SystemStatusPill()

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEEF1)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Profile Photo",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = Color(0xFF111827),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "PNG or JPG, up to 2MB.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = dmSans,
                            fontSize = 12.sp,
                        ),
                        color = Color(0xFF6B7280),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF3F4F6),
                            modifier = Modifier.size(54.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = initials,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = dmSans,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = Color(0xFF111827),
                                )
                            }
                        }

                        Button(
                            onClick = {
                                uploadError = null
                                onUploadPhotoClick()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF111827),
                            ),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                            enabled = !isSaving,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CloudUpload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Upload photo",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                            )
                        }
                    }
                    uploadError?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = dmSans),
                            color = Color(0xFFD92D20),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEEF1)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Personal Business and Information.",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = Color(0xFF111827),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "This information is used across your EnergyIQ account.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = dmSans,
                            fontSize = 12.sp,
                        ),
                        color = Color(0xFF6B7280),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isEditing) {
                        Button(
                    onClick = {
                        snapshotFullName = fullName
                        snapshotBusinessName = businessName
                        snapshotBusinessType = businessType
                        snapshotState = state
                        snapshotCity = city
                        snapshotAiLanguage = aiLanguage
                        isEditing = true
                    },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF111827),
                                contentColor = Color.White,
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 12.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Edit",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Button(
                                onClick = {
                                    fullName = snapshotFullName
                                    businessName = snapshotBusinessName
                                    businessType = snapshotBusinessType
                                    state = snapshotState
                                    city = snapshotCity
                                    aiLanguage = snapshotAiLanguage
                                    isEditing = false
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !isSaving,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF111827),
                                ),
                                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(vertical = 14.dp),
                            ) {
                                Text(
                                    text = "Cancel",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = dmSans,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                )
                            }

                            Button(
                                onClick = {
                                    onSaveChanges(
                                        fullName,
                                        businessName,
                                        businessType,
                                        state,
                                        city,
                                        aiLanguage,
                                    )
                                    isEditing = false
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !isSaving,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF111827),
                                    contentColor = Color.White,
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(vertical = 14.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = if (isSaving) "Saving..." else "Save\nChanges",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = dmSans,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ProfileField(
                        label = "Full name",
                        value = fullName,
                        enabled = isEditing,
                        onValueChange = { fullName = it },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ProfileField(
                        label = "Email",
                        value = email,
                        enabled = false, // Usually immutable; requires email verification flows.
                        onValueChange = { email = it },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ProfileField(
                        label = "Business name",
                        value = businessName,
                        enabled = isEditing,
                        onValueChange = { businessName = it },
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    CustomDropdown(

                        value = businessType,

                        options = businessTypeOptions,
                        enabled = isEditing,
                        onSelected = { businessType = it },
                        placeHolder = "Select business type",
                        label = "Business Type",
                        onValueChange = {businessType = it}
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomDropdown(
                        label = "State",
                        placeHolder = "Select state",
                        value = state,
                        enabled = isEditing,
                        options = stateOptions,
                        onSelected = {
                            state = it
                            city = ""
                        },
                        onValueChange = {
                            state = it
                            city = ""
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val cityOptions = remember(state) {
                        if (state.isBlank()) emptyList()
                        else NigeriaStateCities.citiesFor(state).ifEmpty { listOf("Other") }
                    }
                    CustomDropdown(
                        label = "City",
                        placeHolder = if (state.isBlank()) "Select state first" else "Select city",
                        value = city,
                        enabled = isEditing && state.isNotBlank(),
                        options = cityOptions,
                        onSelected = { city = it },
                        onValueChange = { city = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    CustomDropdown(
                        label = "AI Language",
                        placeHolder = "Select language",
                        value = aiLanguage,
                        enabled = isEditing,
                        options = aiLanguageOptions,
                        onSelected = { aiLanguage = it },
                        onValueChange = { aiLanguage = it },
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SystemStatusPill() {
    val dmSans = dmSansFontFamily()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFECEEF1)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFF22C55E), CircleShape),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "All systems is working fine",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    ),
                    color = Color(0xFF16A34A),
                )
            }
            Text(
                text = "2 min ago",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = dmSans,
                    fontSize = 12.sp,
                ),
                color = Color(0xFF6B7280),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileField(
    label: String,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    trailingHint: String? = null,
) {
    val dmSans = dmSansFontFamily()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            ),
            color = Color(0xFF111827),
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
        )
        if (trailingHint != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = trailingHint,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = dmSans,
                    fontSize = 11.sp,
                ),
                color = Color(0xFF9CA3AF),
            )
        }
    }
}
