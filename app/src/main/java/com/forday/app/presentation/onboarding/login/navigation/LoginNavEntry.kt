package com.forday.app.presentation.onboarding.login.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobby
import com.forday.app.presentation.onboarding.login.LoginRoute
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.frequencyselect.navigation.SelectPerWeek
import com.forday.app.presentation.onboarding.purposeselect.navigation.SelectPurpose
import com.forday.app.presentation.onboarding.termsagreement.navigation.TermsAgreement
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.forday.app.presentation.onboarding.timeselect.navigation.SelectPerTime
import com.forday.app.presentation.home.navigation.Home
import timber.log.Timber

fun EntryProviderScope<NavKey>.loginNavEntry(
    navigator: Navigator,
    onboardingViewModel: OnboardingViewModel,
) {
    nonTabEntry<Login> {
        LoginRoute(
            onNavigateToHome = {
                Timber.e("MainFlow(Login) - navigate to Home requested")
                navigator.resetTo(Home)
            },
            onNavigateToOnboarding = {
                Timber.e("MainFlow(Login) - navigate to SelectHobby requested")
                navigator.navigate(SelectHobby)
            },
            onNavigateToNickname = {
                Timber.e("MainFlow(Login) - navigate to SelectPeriod requested")
                navigator.resetTo(
                    route = SelectPeriod(mode = ScreenMode.ONBOARDING),
                    preloadStack = listOf(
                        SelectHobby,
                        SelectPerTime(mode = ScreenMode.ONBOARDING),
                        SelectPurpose,
                        SelectPerWeek(mode = ScreenMode.ONBOARDING),
                        SelectPeriod(mode = ScreenMode.ONBOARDING),
                    )
                )
            },
            onNavigateToTerms = {
                Timber.e("MainFlow(Login) - navigate to TermsAgreement requested")
                navigator.navigate(TermsAgreement)
            },
            viewModel = onboardingViewModel
        )
    }
}
