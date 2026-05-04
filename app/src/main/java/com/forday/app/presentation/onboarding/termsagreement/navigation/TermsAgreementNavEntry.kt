package com.forday.app.presentation.onboarding.termsagreement.navigation

import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.termsagreement.screen.TermsAgreementRoute

fun EntryProviderScope<NavKey>.termsAgreementNavEntry(
    navigator: Navigator,
    onboardingViewModel: OnboardingViewModel,
) {
    nonTabEntry<TermsAgreement>(backgroundColor = Color(0xFFF9F9F9)) {
        TermsAgreementRoute(
            onBackClick = { navigator.goBack() },
            viewModel = onboardingViewModel
        )
    }
}
