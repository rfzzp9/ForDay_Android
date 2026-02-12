package com.forday.app.presentation.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.onboarding.OnboardingViewModel
import timber.log.Timber

@Composable
fun AppEntryPoint(onboardingViewModel: OnboardingViewModel) {
    val uiState by onboardingViewModel.uiState.collectAsStateWithLifecycle()

    ForDayTheme {
        uiState.initialRoute?.let { route ->
            Timber.e("@@@@@@@@@@@ $route")
            MainFlow(initialRoute = route, onboardingViewModel = onboardingViewModel)
        }
    }
}