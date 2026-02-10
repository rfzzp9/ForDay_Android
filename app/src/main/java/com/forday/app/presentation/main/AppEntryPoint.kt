package com.forday.app.presentation.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.splash.SplashScreen
import timber.log.Timber

@Composable
fun AppEntryPoint() {
    val viewModel: OnboardingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ForDayTheme {
        when {
            uiState.isSplashLoading || uiState.initialRoute == null -> {
                SplashScreen()
            }
            else -> {
                // MainFlow 시작
                Timber.e("@@@@@@@@@@@ "+uiState.initialRoute)
                MainFlow(initialRoute = uiState.initialRoute!!, onboardingViewModel = viewModel)
            }
        }
    }
}