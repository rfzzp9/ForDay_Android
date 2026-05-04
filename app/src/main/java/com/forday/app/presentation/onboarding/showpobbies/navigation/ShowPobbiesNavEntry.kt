package com.forday.app.presentation.onboarding.showpobbies.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.findActivity
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.onboarding.OnboardingFlowViewModel
import com.forday.app.presentation.onboarding.showpobbies.ShowPobbiesScreen

fun EntryProviderScope<NavKey>.showPobbiesNavEntry(
    navigator: Navigator,
    onboardingFlowViewModel: OnboardingFlowViewModel,
) {
    nonTabEntry<ShowPobbies> {
        val context = LocalContext.current
        BackHandler(enabled = true) {
            context.findActivity()?.finish()
        }
        ShowPobbiesScreen(
            onNext = { navigator.navigate(com.forday.app.presentation.onboarding.nicknameinput.navigation.InputNickname) },
            viewModel = onboardingFlowViewModel
        )
    }
}
