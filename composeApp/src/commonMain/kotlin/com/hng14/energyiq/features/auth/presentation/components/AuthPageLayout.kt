package com.hng14.energyiq.features.auth.presentation.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.ui.LocalAdaptiveScreenSpec
import com.hng14.energyiq.core.ui.AuthWaveDecoration
import com.hng14.energyiq.core.ui.EnergyIqBrandMark
import com.hng14.energyiq.core.ui.adaptiveScreenSpec

@Composable
fun AuthPageLayout(
    backgroundColor: Color,
    verticalPadding: Dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val adaptiveSpec = adaptiveScreenSpec(maxWidth)

            CompositionLocalProvider(LocalAdaptiveScreenSpec provides adaptiveSpec) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures {
                                focusManager.clearFocus()
                            }
                        },
                ) {
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
                            .padding(horizontal = 20.dp, vertical = verticalPadding)
                            .widthIn(max = adaptiveSpec.contentMaxWidth)
                            .align(Alignment.TopCenter),
                        horizontalAlignment = horizontalAlignment,
                        content = content,
                    )
                }
            }
        }
    }
}

@Composable
fun AuthBrandHeader(
    topSpacing: Dp = 12.dp,
) {
    Spacer(modifier = Modifier.height(topSpacing))
    EnergyIqBrandMark(modifier = Modifier.fillMaxWidth())
}

@Composable
fun RegisterHeader() {
    val adaptiveSpec = LocalAdaptiveScreenSpec.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Create An Account",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = adaptiveSpec.headlineSize),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Take control of your energy,\nstarting today.",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.W400,
                fontSize = adaptiveSpec.bodySize,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun AuthDivider() {
    val dmSans = dmSansFontFamily()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
        )
        Text(
            text = "OR",
            modifier = Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp,
            ),
            color = Color(0xFF6B7280),
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
        )
    }
}
