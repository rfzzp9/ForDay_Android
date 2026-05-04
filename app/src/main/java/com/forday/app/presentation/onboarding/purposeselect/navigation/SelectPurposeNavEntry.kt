package com.forday.app.presentation.onboarding.purposeselect.navigation

import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.onboarding.OnboardingFlowViewModel
import com.forday.app.presentation.onboarding.frequencyselect.navigation.SelectPerWeek
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.forday.app.presentation.onboarding.purposeselect.SelectPurposeRoute

fun EntryProviderScope<NavKey>.selectPurposeNavEntry(
    navigator: Navigator,
    onboardingFlowViewModel: OnboardingFlowViewModel,
) {
    nonTabEntry<SelectPurpose>(backgroundColor = Color(0xFFF9F9F9)) {
        SelectPurposeRoute(
            onNext = { navigator.navigate(SelectPerWeek(mode = ScreenMode.ONBOARDING)) },
            onBack = { navigator.goBack() },
            viewModel = onboardingFlowViewModel
        )
    }
}
