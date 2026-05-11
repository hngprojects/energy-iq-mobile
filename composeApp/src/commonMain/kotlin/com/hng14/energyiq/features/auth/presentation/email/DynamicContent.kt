package com.hng14.energyiq.features.auth.presentation.email


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val BrandOrange    = Color(0xFFF97316)
private val TextPrimary    = Color(0xFF111111)
private val TextSecondary  = Color(0xFF555555)
private val OtpBg          = Color(0xFFF0F0F0)
private val CardWhite      = Color(0xFFFFFFFF)


//title
@Composable
private fun EmailTitle(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 16.dp),
    )
}

//greeting which for now takes amaka
@Composable
private fun Greeting(name: String) {
    Text(
        text = "Hi $name,",
        fontSize = 14.sp,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

/** bodyText / message1 the main paragraph that changes per state, before the otp/button */
@Composable
private fun BodyText(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = TextSecondary,
        lineHeight = 20.sp,
        modifier = Modifier.padding(bottom = 20.dp),
    )
}

/** OTP block — shown only on ConfirmEmailAccount state */
@Composable
private fun OtpBlock(otp: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(OtpBg, RoundedCornerShape(8.dp))
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = otp,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 4.sp,
        )
    }
    Spacer(Modifier.height(20.dp))
}

/** Action button which is shown on ResetPassword, VerificationLinkExpired, UpdatedPassword,
 *  and IsVerificationSuccess. Label and action are decided per state. */
@Composable
private fun ActionButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = TextPrimary),
    ) {
        Text(text = label, fontSize = 15.sp, color = CardWhite)
    }
    Spacer(Modifier.height(20.dp))
}

/** Secondary message block below OTP / button */
@Composable
private fun SecondaryText(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = TextSecondary,
        lineHeight = 18.sp,
        modifier = Modifier.padding(bottom = 20.dp),
    )
}

/** Feature highlights row that is shown only on IsVerificationSuccess */
@Composable
private fun FeatureHighlights() {
    val features = listOf(
        "📊" to "Monitor in real time\nTrack your solar generation, battery level and energy usage.",
        "💡" to "Get valuable insights\nUnderstand your energy patterns and improve efficiency.",
        "💰" to "Save more\nOptimise your energy and reduce costs.",
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(bottom = 20.dp)) {
        features.forEach { (emoji, text) ->
            Row(verticalAlignment = Alignment.Top) {
                Text(text = emoji, fontSize = 18.sp, modifier = Modifier.padding(end = 10.dp, top = 2.dp))
                Text(text = text, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)
            }
        }
    }
}

/** "Didn't create / request this?" safety block */
@Composable
private fun DidntMakeThisBlock(message: String) {
    Text(
        text = message,
        fontSize = 13.sp,
        color = TextSecondary,
        lineHeight = 18.sp,
        modifier = Modifier.padding(bottom = 20.dp),
    )
}



//State 1 — Email verification OTP sent after registration.
@Composable
fun ConfirmEmailAccountContent(
    name: String = "Amaka",
    otp: String = "377343",
    //onResend: () -> Unit = {},
) {
    EmailTitle("Email Verification")
    Greeting(name)
    BodyText(
        "Thanks for signing up for ENERGYIQ! Use the verification code below to confirm your email address."
    )
    OtpBlock(otp)
    SecondaryText("This code will expire in 5 minutes.\nEnter this code in the app to complete your account setup.")
    DidntMakeThisBlock(
        "Didn't create an account?\nIf you didn't create an account with EnergyIQ, you can kindly ignore this email."
    )
}


//State 2 — Password reset link email.
@Composable
fun ResetPasswordContent(
    name: String = "Amaka",
    onResetClick: () -> Unit = {},
) {
    EmailTitle("Reset Password")
    Greeting(name)
    BodyText("We received a request to reset your password. Click the button below to create a new password.")
    ActionButton(label = "Reset Password", onClick = onResetClick)
    SecondaryText("This link will expire in 5 minutes and can only be used once.")
    DidntMakeThisBlock(
        "Didn't create an account?\nIf you didn't create an account with EnergyIQ, you can kindly ignore this email."
    )
}

//State 3 — Reset link has expired.

@Composable
fun VerificationLinkExpiredContent(
    name: String = "Amaka",
    onResendClick: () -> Unit = {},
) {
    EmailTitle("The link has expired")
    Greeting(name)
    BodyText("Your password reset has expired or has already been used.")
    ActionButton(label = "Resend link", onClick = onResendClick)
    SecondaryText(
        "For security reasons, password reset links expire after 10 minutes and can only be used once."
    )
    DidntMakeThisBlock(
        "Didn't create an account?\nIf you didn't request a password reset, you can safely ignore this email."
    )
}


//State 4 — Password updated successfully.

@Composable
fun UpdatedPasswordContent(
    name: String = "Amaka",
    onLoginClick: () -> Unit = {},
) {
    EmailTitle("Password updated\nsuccessfully")
    Greeting(name)
    BodyText("Your password reset has been changed successfully. You're all set.")
    ActionButton(label = "Login", onClick = onLoginClick)
    DidntMakeThisBlock(
        "Didn't make this Change?\nIf you did not make this change, please reset your password immediately or contact support."
    )
}


//State 5 — Account verified, welcome screen.

@Composable
fun IsVerificationSuccessContent(
    name: String = "Amaka",
    onLoginClick: () -> Unit = {}, //nav to login screen
) {
    EmailTitle("Welcome to EnergyIQ")
    Greeting(name)
    BodyText("Your account has been created successfully and your inverter is connected.")
    ActionButton(label = "Login", onClick = onLoginClick)
    FeatureHighlights()
}