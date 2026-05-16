package com.forday.app.presentation.onboarding.nicknameinput.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.onboarding.OnboardingFlowViewModel
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.experiment.OnboardingAbNavigationPolicy
import com.forday.app.presentation.onboarding.nicknameinput.InputNicknameRoute

fun EntryProviderScope<NavKey>.inputNicknameNavEntry(
    navigator: Navigator,
    onboardingViewModel: OnboardingViewModel,
    onboardingFlowViewModel: OnboardingFlowViewModel,
) {
    nonTabEntry<InputNickname> {
        InputNicknameRoute(
            onNext = { userName ->
                val nextRoute = OnboardingAbNavigationPolicy.routeAfterNicknameRegistration(
                    variant = onboardingViewModel.uiState.value.onboardingAbVariant,
                    userName = userName,
                )
                if (nextRoute == Home) {
                    navigator.resetTo(Home)
                } else {
                    navigator.navigate(nextRoute)
                }
            },
            viewModel = onboardingFlowViewModel
        )
    }
}
