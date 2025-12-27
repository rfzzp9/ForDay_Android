package com.forday.app.presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.forday.app.core.extension.ObserveAsSideEffects

@Composable
internal fun OnBoardingRoute(
    onClose: () -> Unit,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when(state) {
        is ExampleModel -> {
            OnboardingScreen(
                state = state,
                onAction = { action ->
                    when(action) {
                        is ExampleAction.OnClose -> onClose()
                    }
                }
            )
        }
        else -> {
            LoginScreen(
                onKakaoLoginClick = {},
                onGuestModeClick = {},
            )
        }
    }

    ObserveAsSideEffects(flow = viewModel.sideEffect) { events ->
        when (events) {
            is ExampleSideEffect.OnClose -> Unit
            is ExampleSideEffect.Error -> Unit
        }
    }
}

@Composable
private fun OnboardingScreen(
    state: ExampleModel,
    onAction: (ExampleAction) -> Unit
) {

}