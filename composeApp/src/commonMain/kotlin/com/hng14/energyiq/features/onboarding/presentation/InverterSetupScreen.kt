package com.hng14.energyiq.features.onboarding.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.ui.AuthWaveDecoration
import com.hng14.energyiq.core.ui.EnergyIqBrandMark
import com.hng14.energyiq.core.ui.InverterCardIcon
import com.hng14.energyiq.core.ui.LocalAdaptiveScreenSpec
import com.hng14.energyiq.core.ui.ComingSoonDialog
import com.hng14.energyiq.core.ui.OnboardingSuccessIllustration
import com.hng14.energyiq.core.ui.ServerErrorDialog
import com.hng14.energyiq.core.ui.adaptiveScreenSpec
import com.hng14.energyiq.features.auth.presentation.components.AuthTextField
import com.hng14.energyiq.features.auth.presentation.components.PasswordTextField
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.CompositionLocalProvider
import org.koin.compose.viewmodel.koinViewModel

private enum class InverterSetupStep {
    SELECT,
    CONNECTION,
    SUCCESS,
}

private enum class ConnectionFieldType {
    TEXT,
    PASSWORD,
    EMAIL,
    NUMBER,
}

private sealed interface VictronTestState {
    data object Idle : VictronTestState
    data class Running(val stepIndex: Int) : VictronTestState
    data class Failure(val message: String) : VictronTestState
    data object Success : VictronTestState
}

private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private val victronTestSteps = listOf(
    "Verifying credentials and reaching Cloud API",
    "Authenticating credentials",
    "Fetching installation data",
    "Requesting live data",
    "Connection complete",
)

private data class ConnectionField(
    val id: String,
    val label: String,
    val placeholder: String,
    val type: ConnectionFieldType = ConnectionFieldType.TEXT,
    val optional: Boolean = false,
    val readOnly: Boolean = false,
    val initialValue: String = "",
)

private data class ConnectionContent(
    val title: String,
    val subtitle: String,
    val fields: List<ConnectionField>,
    val primaryButtonText: String,
    val helperLines: List<String>,
)

private data class InverterOption(
    val title: String,
    val subtitle: String,
    val connection: ConnectionContent,
)

private fun inverterOptionFor(title: String): InverterOption? {
    return when (title.lowercase()) {
        "victron" -> InverterOption(
        title = "Victron",
        subtitle = "Vrm OAuth",
        connection = ConnectionContent(
            title = "Victron Inverter\nConnection",
            subtitle = "Enter specific details of your inverter type",
            fields = listOf(
                ConnectionField(
                    id = "vrm_email",
                    label = "Enter VRM Email",
                    placeholder = "you@email.com",
                    type = ConnectionFieldType.EMAIL,
                ),
                ConnectionField(
                    id = "vrm_password",
                    label = "Enter VRM Password",
                    placeholder = "************",
                    type = ConnectionFieldType.PASSWORD,
                ),
                ConnectionField(
                    id = "vrm_api_token",
                    label = "Enter VRM API Token",
                    placeholder = "************",
                    type = ConnectionFieldType.PASSWORD,
                ),
            ),
            primaryButtonText = "Connect",
            helperLines = listOf(
                "Use your VRM login details",
                "Find your API token in your Profile -> API Access Tokens -> Generate token",
            ),
        ),
        )
        "luminous" -> InverterOption(
        title = "Luminous",
        subtitle = "No API",
        connection = ConnectionContent(
            title = "Luminous Inverter\nConnection",
            subtitle = "Enter number specific to your inverter type",
            fields = listOf(
                ConnectionField(
                    id = "model",
                    label = "Enter Inverter Model",
                    placeholder = "e.g Luminous Eco Volt",
                ),
                ConnectionField(
                    id = "capacity",
                    label = "Enter Inverter Capacity (kVA/kW)",
                    placeholder = "3.5kVA",
                    type = ConnectionFieldType.NUMBER,
                ),
                ConnectionField(
                    id = "battery_bank",
                    label = "Enter Battery Bank Size (Ah or kWh)",
                    placeholder = "e.g 8 x 200Ah, 96V",
                    type = ConnectionFieldType.NUMBER,
                ),
                ConnectionField(
                    id = "solar_array",
                    label = "Enter Solar Array Size (kWp)",
                    placeholder = "e.g 8",
                    type = ConnectionFieldType.NUMBER,
                ),
            ),
            primaryButtonText = "Save Inverter",
            helperLines = listOf(
                "Model: use the inverter name printed on the unit, e.g. Luminous 5kVA",
                "Capacity: enter the rated output, e.g. 5kVA or 3.5kW",
                "Battery Bank Size: enter values like 2 x 200Ah or 4.8kWh",
                "Solar Array Size: enter values like 1.8kWp or 4 x 450W",
            ),
        ),
        )
        "growatt" -> InverterOption(
        title = "Growatt",
        subtitle = "API key",
        connection = ConnectionContent(
            title = "Growatt Inverter\nConnection",
            subtitle = "Enter your Growatt API details",
            fields = listOf(
                ConnectionField(
                    id = "growatt_api_token",
                    label = "Enter Growatt API Token",
                    placeholder = "zi2o82...",
                    type = ConnectionFieldType.PASSWORD,
                ),
            ),
            primaryButtonText = "Save Inverter",
            helperLines = listOf(
                "API Token: use the API key provided in your Growatt Open API account settings",
            ),
        ),
        )
        "su-kam", "sukam", "su kam" -> InverterOption(
        title = "Su-kam",
        subtitle = "No API",
        connection = ConnectionContent(
            title = "Su-kam Inverter\nConnection",
            subtitle = "Enter number specific to your inverter type",
            fields = listOf(
                ConnectionField(
                    id = "model",
                    label = "Enter Inverter Model",
                    placeholder = "e.g Su-Kam Falcon+",
                ),
                ConnectionField(
                    id = "capacity",
                    label = "Enter Inverter Capacity (kVA/kW)",
                    placeholder = "3.5kVA",
                    type = ConnectionFieldType.NUMBER,
                ),
                ConnectionField(
                    id = "battery_bank",
                    label = "Enter Battery Bank Size (Ah or kWh)",
                    placeholder = "e.g 8 x 200Ah, 96V",
                    type = ConnectionFieldType.NUMBER,
                ),
                ConnectionField(
                    id = "solar_array",
                    label = "Enter Solar Array Size (kWp)",
                    placeholder = "e.g 8",
                    type = ConnectionFieldType.NUMBER,
                ),
            ),
            primaryButtonText = "Save Inverter",
            helperLines = listOf(
                "Model: use the inverter name printed on the unit, e.g. Su-Kam Falcon+",
                "Capacity: enter the rated output, e.g. 5kVA or 3.5kW",
                "Battery Bank Size: enter values like 2 x 200Ah or 4.8kWh",
                "Solar Array Size: enter values like 1.8kWp or 4 x 450W",
            ),
        ),
        )
        "sunsynk" -> InverterOption(
        title = "Sunsynk",
        subtitle = "API key",
        connection = ConnectionContent(
            title = "Sunsynk Inverter\nConnection",
            subtitle = "Enter specific details of your inverter type",
            fields = listOf(
                ConnectionField(
                    id = "solarman_app_id",
                    label = "Enter SolarMan App ID",
                    placeholder = "e.g 123456",
                    type = ConnectionFieldType.NUMBER,
                ),
                ConnectionField(
                    id = "solarman_email",
                    label = "Enter SolarMan Email",
                    placeholder = "you@email.com",
                    type = ConnectionFieldType.EMAIL,
                ),
                ConnectionField(
                    id = "solarman_password",
                    label = "Enter SolarMan Password",
                    placeholder = "************",
                    type = ConnectionFieldType.PASSWORD,
                ),
            ),
            primaryButtonText = "Save Inverter",
            helperLines = listOf(
                "SolarMan App ID: enter the numeric app or plant identifier, e.g. 123456",
                "SolarMan Email: use the email linked to your SolarMan account",
                "SolarMan Password: use your SolarMan account password",
            ),
            ),
        )
        "deye" -> InverterOption(
        title = "Deye",
        subtitle = "API key",
        connection = ConnectionContent(
            title = "Deye Inverter\nConnection",
            subtitle = "Enter specific details of your inverter type",
            fields = listOf(
                ConnectionField(
                    id = "solarman_app_id",
                    label = "Enter SolarMan App ID",
                    placeholder = "e.g 123456",
                    type = ConnectionFieldType.NUMBER,
                ),
                ConnectionField(
                    id = "solarman_email",
                    label = "Enter SolarMan Email",
                    placeholder = "you@email.com",
                    type = ConnectionFieldType.EMAIL,
                ),
                ConnectionField(
                    id = "solarman_password",
                    label = "Enter SolarMan Password",
                    placeholder = "************",
                    type = ConnectionFieldType.PASSWORD,
                ),
            ),
            primaryButtonText = "Save Inverter",
            helperLines = listOf(
                "SolarMan App ID: enter the numeric app or plant identifier, e.g. 123456",
                "SolarMan Email: use the email linked to your SolarMan account",
                "SolarMan Password: use your SolarMan account password",
            ),
        ),
    )
        "sandbox" -> InverterOption(
        title = "Sandbox",
        subtitle = "Mock API",
        connection = ConnectionContent(
            title = "Sandbox Inverter\nConnection",
            subtitle = "Enter your sandbox credentials",
            fields = listOf(
                ConnectionField(
                    id = "brand",
                    label = "Brand",
                    placeholder = "SANDBOX",
                    type = ConnectionFieldType.TEXT,
                    readOnly = true,
                    initialValue = "SANDBOX",
                ),
                ConnectionField(
                    id = "sandbox_access_token",
                    label = "Sandbox Access Token",
                    placeholder = "mock-token-a",
                    type = ConnectionFieldType.PASSWORD,
                ),
            ),
            primaryButtonText = "Connect",
            helperLines = listOf(
                "Enter the sandbox access token provided to you",
            ),
        ),
    )
        else -> InverterOption(
            title = title.lowercase().replaceFirstChar { it.titlecase() },
            subtitle = "We'll guide",
            connection = ConnectionContent(
            title = "${title.lowercase().replaceFirstChar { it.titlecase() }} Inverter\nConnection",
            subtitle = "Enter number specific to your inverter type",
            fields = listOf(
                ConnectionField(
                    id = "model",
                    label = "Enter Inverter Model",
                    placeholder = "e.g Hybrid 5kVA",
                ),
                ConnectionField(
                    id = "capacity",
                    label = "Enter Inverter Capacity (kVA/kW)",
                    placeholder = "3.5kVA",
                    type = ConnectionFieldType.NUMBER,
                ),
                ConnectionField(
                    id = "battery_bank",
                    label = "Enter Battery Bank Size (Ah or kWh)",
                    placeholder = "e.g 8 x 200Ah, 96V",
                    type = ConnectionFieldType.NUMBER,
                ),
                ConnectionField(
                    id = "solar_array",
                    label = "Enter Solar Array Size (kWp)",
                    placeholder = "e.g 8",
                    type = ConnectionFieldType.NUMBER,
                ),
            ),
            primaryButtonText = "Save Inverter",
            helperLines = listOf(
                "Model: use the inverter name printed on the unit, e.g. Hybrid 5kVA",
                "Capacity: enter the rated output, e.g. 5kVA or 3.5kW",
                "Battery Bank Size: enter values like 2 x 200Ah or 4.8kWh",
                "Solar Array Size: enter values like 1.8kWp or 4 x 450W",
            ),
            ),
        )
    }
}

@Composable
fun InverterSetupScreen(
    onComplete: () -> Unit,
) {
    val viewModel = koinViewModel<InverterSetupViewModel>()
    val setupState by viewModel.state.collectAsState()
    var step by remember { mutableStateOf(InverterSetupStep.SELECT) }
    var selectedOption by remember { mutableStateOf<InverterOption?>(null) }
    var connectionValues by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var victronTestState by remember { mutableStateOf<VictronTestState>(VictronTestState.Idle) }
    var victronTestJob by remember { mutableStateOf<Job?>(null) }
    var showComingSoonFor by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val inverterOptions = remember(setupState.supportedBrands) {
        setupState.supportedBrands.mapNotNull(::inverterOptionFor).distinctBy { it.title }
    }

    setupState.connectError?.let { message ->
        ServerErrorDialog(
            message = message,
            onDismiss = viewModel::onConnectErrorDismissed,
        )
    }

    showComingSoonFor?.let { brand ->
        ComingSoonDialog(
            featureName = brand,
            onDismiss = { showComingSoonFor = null }
        )
    }

    when (step) {
        InverterSetupStep.SELECT -> InverterSelectionContent(
            supportedBrands = inverterOptions,
            isLoading = setupState.isLoading,
            errorMessage = setupState.errorMessage,
            selectedOption = selectedOption,
            onSelectOption = { selectedOption = it },
            onRetry = viewModel::loadSupportedBrands,
            onContinue = {
                selectedOption?.let { option ->
                    if (option.title != "Sandbox" && option.title != "Growatt") {
                        showComingSoonFor = option.title
                        return@InverterSelectionContent
                    }
                    connectionValues = option.connection.fields.associate { field -> field.id to field.initialValue }
                    victronTestJob?.cancel()
                    victronTestState = VictronTestState.Idle
                    step = InverterSetupStep.CONNECTION
                }
            },
        )

        InverterSetupStep.CONNECTION -> {
            val option = selectedOption
            if (option != null) {
                InverterConnectionContent(
                    option = option,
                    values = connectionValues,
                    victronTestState = victronTestState,
                    isSubmitting = setupState.isConnecting,
                    onValueChange = { fieldId, value ->
                        connectionValues = connectionValues + (fieldId to value)
                        if (option.title == "Victron") {
                            victronTestJob?.cancel()
                            victronTestState = VictronTestState.Idle
                        }
                    },
                    onRunVictronTest = {
                        victronTestJob?.cancel()
                        victronTestJob = coroutineScope.launch {
                            victronTestSteps.forEachIndexed { index, _ ->
                                victronTestState = VictronTestState.Running(stepIndex = index)
                                delay(900)
                            }
                            victronTestState = VictronTestState.Success
                        }
                    },
                    onBack = { step = InverterSetupStep.SELECT },
                    onSubmit = {
                        viewModel.connectInverter(
                            brand = option.title,
                            values = connectionValues,
                            onSuccess = { step = InverterSetupStep.SUCCESS }
                        )
                    },
                )
            }
        }

        InverterSetupStep.SUCCESS -> InverterSavedSuccessContent(onContinue = onComplete)
    }
}

@Composable
private fun InverterSelectionContent(
    supportedBrands: List<InverterOption>,
    isLoading: Boolean,
    errorMessage: String?,
    selectedOption: InverterOption?,
    onSelectOption: (InverterOption) -> Unit,
    onRetry: () -> Unit,
    onContinue: () -> Unit,
) {
    // Only tighten spacing on the selection screen so "What type of inverter..." sits higher.
    SetupPageLayout(topSpacer = 12.dp, afterBrandSpacer = 8.dp) {
        val adaptiveSpec = LocalAdaptiveScreenSpec.current
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "What type of inverter do\nyou use",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = adaptiveSpec.headlineSize,
                        lineHeight = 30.sp,
                    ),
                    color = Color(0xFF1F2430),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Select your inverter so we can tailor your\nexperience",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = adaptiveSpec.bodySize,
                        lineHeight = 22.sp,
                    ),
                    color = Color(0xFF7B8190),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(32.dp))

                when {
                    isLoading -> {
                        CircularProgressIndicator(color = Color(0xFF141D2F))
                    }

                    errorMessage != null -> {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFD92D20),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(onClick = onRetry) {
                            Text(text = "Retry")
                        }
                    }

                    supportedBrands.isEmpty() -> {
                        Text(
                            text = "No supported inverter brands are available right now.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF7B8190),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    else -> FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        maxItemsInEachRow = adaptiveSpec.inverterGridColumns,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        supportedBrands.forEach { option ->
                            val isSelected = selectedOption == option
                            Surface(
                                modifier = Modifier
                                    .width(adaptiveSpec.inverterCardWidth)
                                    .clickable { onSelectOption(option) },
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF6FD08C) else Color(0xFFE5E7EB),
                                ),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    InverterCardIcon(
                                        modifier = Modifier.size(width = 36.dp, height = 28.dp),
                                        contentDescription = "${option.title} icon",
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = option.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            fontSize = if (adaptiveSpec.tier == com.hng14.energyiq.core.ui.WidthTier.COMPACT) 16.sp else 17.sp,
                                        ),
                                        color = Color(0xFF374151),
                                        textAlign = TextAlign.Center,
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = option.subtitle,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                        ),
                                        color = Color(0xFF9CA3AF),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onContinue,
            enabled = selectedOption != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(adaptiveSpec.buttonHeight),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF141D2F),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFCBD0D8),
                disabledContentColor = Color.White,
            ),
        ) {
            Text(text = "Continue", fontSize = 16.sp)
        }
    }
}

@Composable
private fun InverterConnectionContent(
    option: InverterOption,
    values: Map<String, String>,
    victronTestState: VictronTestState,
    isSubmitting: Boolean,
    onValueChange: (String, String) -> Unit,
    onRunVictronTest: () -> Unit,
    onBack: () -> Unit,
    onSubmit: () -> Unit,
) {
    val content = option.connection
    val hasValidFields = content.fields.all { field ->
        isConnectionFieldValid(field = field, value = values[field.id].orEmpty())
    }
    val canSubmit = when (option.title) {
        "Victron" -> hasValidFields && victronTestState is VictronTestState.Success
        else -> hasValidFields
    }

    SetupPageLayout {
        val adaptiveSpec = LocalAdaptiveScreenSpec.current
        Text(
            text = content.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = adaptiveSpec.headlineSize,
                lineHeight = 30.sp,
            ),
            color = Color(0xFF1F2430),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = content.subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = adaptiveSpec.bodySize,
                lineHeight = 22.sp,
            ),
            color = Color(0xFF7B8190),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(28.dp))

        content.fields.forEachIndexed { index, field ->
            when (field.type) {
                ConnectionFieldType.PASSWORD -> PasswordTextField(
                    value = values[field.id].orEmpty(),
                    onValueChange = { onValueChange(field.id, it) },
                    label = field.label,
                    placeholder = field.placeholder,
                    showStatusIndicator = false,
                    imeAction = if (index == content.fields.lastIndex) ImeAction.Done else ImeAction.Next,
                    modifier = Modifier.fillMaxWidth(),
                )

                ConnectionFieldType.EMAIL, ConnectionFieldType.TEXT, ConnectionFieldType.NUMBER -> AuthTextField(
                    value = values[field.id].orEmpty(),
                    onValueChange = { onValueChange(field.id, it) },
                    label = field.label,
                    placeholder = field.placeholder,
                    keyboardType = when (field.type) {
                        ConnectionFieldType.EMAIL -> KeyboardType.Email
                        ConnectionFieldType.NUMBER -> KeyboardType.Number
                        else -> KeyboardType.Text
                    },
                    imeAction = if (index == content.fields.lastIndex) ImeAction.Done else ImeAction.Next,
                    showStatusIndicator = false,
                    readOnly = field.readOnly,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (index != content.fields.lastIndex) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (option.title == "Victron" && hasValidFields) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = victronTestState !is VictronTestState.Running) {
                        onRunVictronTest()
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "•",
                    color = Color(0xFF111827),
                    fontSize = 20.sp,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Test connection to Victron VRM",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    ),
                    color = when (victronTestState) {
                        VictronTestState.Success -> Color(0xFF2EAF5D)
                        is VictronTestState.Failure -> Color(0xFFD92D20)
                        else -> Color(0xFF374151)
                    },
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = when (victronTestState) {
                        VictronTestState.Success -> Color(0xFF2EAF5D)
                        is VictronTestState.Failure -> Color(0xFFD92D20)
                        else -> Color(0xFF374151)
                    },
                )
            }

            when (victronTestState) {
                VictronTestState.Idle -> Unit

                is VictronTestState.Running -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = Color(0xFF7B8190),
                            strokeWidth = 2.dp,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = victronTestSteps[victronTestState.stepIndex],
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                            ),
                            color = Color(0xFF7B8190),
                        )
                    }
                }

                is VictronTestState.Failure -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = victronTestState.message,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                        ),
                        color = Color(0xFFD92D20),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                VictronTestState.Success -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Connection Verified",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                        ),
                        color = Color(0xFF2EAF5D),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onSubmit,
            enabled = canSubmit && !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(adaptiveSpec.buttonHeight),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF141D2F),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFCBD0D8),
                disabledContentColor = Color.White,
            ),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(text = content.primaryButtonText, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(adaptiveSpec.buttonHeight),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(text = "Back", color = Color(0xFF2A2F3C), fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Where do I find these?",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            ),
            color = Color(0xFF374151),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(10.dp))
        content.helperLines.forEachIndexed { index, line ->
            Text(
                text = line,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            if (index != content.helperLines.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun InverterSavedSuccessContent(
    onContinue: () -> Unit,
) {
    SetupPageLayout {
        val adaptiveSpec = LocalAdaptiveScreenSpec.current
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "You're All Set",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = adaptiveSpec.headlineSize,
                        lineHeight = 30.sp,
                    ),
                    color = Color(0xFF1F2430),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your EnergyIQ account has been successfully\ncreated and your Energy System is ready to be\nmonitored",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = adaptiveSpec.bodySize,
                        lineHeight = 22.sp,
                    ),
                    color = Color(0xFF7B8190),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(36.dp))

                OnboardingSuccessIllustration(
                    modifier = Modifier.size(if (adaptiveSpec.tier == com.hng14.energyiq.core.ui.WidthTier.EXPANDED) 292.dp else 252.dp),
                    contentDescription = "Success",
                )
            }
        }

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(adaptiveSpec.buttonHeight),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF141D2F),
                contentColor = Color.White,
            ),
        ) {
            Text(text = "Continue", fontSize = 16.sp)
        }
    }
}

private fun isConnectionFieldValid(
    field: ConnectionField,
    value: String,
): Boolean {
    val trimmed = value.trim()

    if (trimmed.isEmpty()) {
        return field.optional
    }

    return when (field.type) {
        ConnectionFieldType.EMAIL -> emailPattern.matches(trimmed)
        ConnectionFieldType.NUMBER -> trimmed.isNotEmpty()
        ConnectionFieldType.TEXT,
        ConnectionFieldType.PASSWORD,
            -> trimmed.isNotEmpty()
    }
}

@Composable
private fun SetupPageLayout(
    topSpacer: Dp = 18.dp,
    afterBrandSpacer: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFAFAF8),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val adaptiveSpec = adaptiveScreenSpec(maxWidth)

            CompositionLocalProvider(LocalAdaptiveScreenSpec provides adaptiveSpec) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AuthWaveDecoration(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = (-10).dp, y = (-10).dp)
                            .size(width = 170.dp, height = 182.dp),
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .imePadding()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .widthIn(max = adaptiveSpec.contentMaxWidth)
                            .align(Alignment.TopCenter),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    content = {
                        Spacer(modifier = Modifier.height(topSpacer))
                        EnergyIqBrandMark(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(afterBrandSpacer))
                        content()
                    },
                )
            }
        }
    }
}
}
