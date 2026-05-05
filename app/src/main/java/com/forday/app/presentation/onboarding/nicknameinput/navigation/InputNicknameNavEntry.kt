package com.forday.app.presentation.onboarding.nicknameinput.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.onboarding.OnboardingFlowViewModel
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.experiment.OnboardingAbVariant
import com.forday.app.presentation.onboarding.experiment.navigation.MyHobbySelect
import com.forday.app.presentation.onboarding.nicknameinput.InputNicknameRoute

fun EntryProviderScope<NavKey>.inputNicknameNavEntry(
    navigator: Navigator,
    onboardingViewModel: OnboardingViewModel,
    onboardingFlowViewModel: OnboardingFlowViewModel,
) {
    nonTabEntry<InputNickname> {
        InputNicknameRoute(
            onNext = {
                if (onboardingViewModel.uiState.value.onboardingAbVariant == OnboardingAbVariant.NEW) {
                    navigator.navigate(MyHobbySelect)
                } else {
                    navigator.resetTo(com.forday.app.presentation.home.navigation.Home)
                }
            },
            viewModel = onboardingFlowViewModel
        )
    }
}
