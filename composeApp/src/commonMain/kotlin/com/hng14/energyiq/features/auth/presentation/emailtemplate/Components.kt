package com.hng14.energyiq.features.auth.presentation.emailtemplate

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CardWhite = Color(0xFFFFFFFF) //onteritary
private val TextPrimary = Color(0xFF111111)
private val FooterText = Color(0xFF1A1A2E)  //lighton surface
private val FooterBg = Color(0xFFD8DBE2)


@Composable
fun FixedContent(
    dynamicContent: @Composable () -> Unit
) {
    Scaffold(
        topBar = { EmailTopBar() },
        bottomBar = { EmailFooter() },
    ) { paddingValues ->

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = Color.White,)
                {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .imePadding()
                            .verticalScroll(state = rememberScrollState())
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.Start,
                    )
                        {
                            EmailImage(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 28.dp),
                                )
                            dynamicContent()
                            Text(
                                text = "Regards,\nThe EnergyIQ Team",
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                            )

                        }
                }
    }
}

@Composable
fun EmailTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(1.5.dp, Color(0xFFF3A847)),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "\u26A1",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFF3A847),
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "ENERGY",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF2A2F3C),
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = "IQ",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFFF3A847),
            fontWeight = FontWeight.Medium,
        )
    }
//        Icon(
//            imageVector = Icons.Default.FlashOn,
//            contentDescription = null,
//            tint = BrandOrange,
//            modifier = Modifier.size(22.dp),
//        )
//        Spacer(Modifier.width(6.dp))
//        Text(
//            text = "ENERGYIQ",
//            //fontWeight = FontWeight.Bold,
//            fontSize = 15.sp,
//            color = TextPrimary,
//            letterSpacing = 1.sp,
//        )
//        Image(
//            painter = painterResource(Res.drawable.logo), // Your XML vector
//            contentDescription = "EnergyIQ logo",
//            modifier = Modifier.height(22.dp),
//            contentScale = ContentScale.Fit
//        )
}
@Composable
fun EmailImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(196.dp)
            .background(Color(0xFFDCFCE7), shape = RoundedCornerShape(50)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color(0xFF22C55E), shape = RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = CardWhite,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Composable
fun EmailFooter(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FooterBg)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
) {
    // Social icons row(to be replaced with real iconsbuttons)
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(bottom = 12.dp),
    ) {
        listOf("f", "in", "X", "💬", "✉").forEach { icon ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(1.dp, FooterText, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = icon, color = FooterText, fontSize = 12.sp)
            }
        }
    }

    Text(
        text = "© 2025 EnergyIQ. All rights reserved.",
        fontSize = 11.sp,
        color = FooterText,
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = "This is an automated email, please do not reply.",
        fontSize = 11.sp,
        color = FooterText,
        textAlign = TextAlign.Center,
    )
}
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_8")
@Composable
fun EmailVerificationScreenPreview() {
    EmailFooter()
}



