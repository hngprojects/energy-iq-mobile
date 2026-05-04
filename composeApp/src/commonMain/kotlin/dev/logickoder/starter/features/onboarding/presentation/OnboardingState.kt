package dev.logickoder.starter.features.onboarding.presentation

data class OnboardingState(
    val currentPage: Int = 0,
    val totalPages: Int = 3,
) {
    val isLastPage: Boolean get() = currentPage == totalPages - 1
}