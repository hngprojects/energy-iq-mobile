package com.hng14.energyiq.features.onboarding

typealias OnOnboardingComplete = () -> Unit

data class OnboardingPage(
    val title: String,
    val description: String,
    val emoji: String,
)

val defaultOnboardingPages = listOf(
    OnboardingPage(
        emoji = "🚀",
        title = "Power Your Distribution Business",
        description = " Manage solar inverter inventory, track sales activity, and keep your operations moving from one place.",
    ),
    OnboardingPage(
        emoji = "🎨",
        title = "Serve Dealers Faster",
        description = "Access product details, pricing, and stock updates quickly so your team can respond to dealers without delays.",
    ),
    OnboardingPage(
        emoji = "📱",
        title = "Grow With Better Insights",
        description = "Monitor orders, distributor performance, and business trends to make smarter decisions and scale confidently.",
    ),
)
