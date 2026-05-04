package com.forday.app.presentation.onboarding.splash.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.onboarding.splash.SplashRoute
import com.forday.app.presentation.onboarding.splash.SplashViewModel

fun EntryProviderScope<NavKey>.splashNavEntry(
    splashViewModel: SplashViewModel,
) {
    entry<Splash> {
        SplashRoute(splashViewModel = splashViewModel)
    }
}
