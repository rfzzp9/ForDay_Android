package com.forday.app.presentation.onboarding.splash

import android.window.SplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dayn.forday.R
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.onboarding.OnboardingViewModel
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
fun SplashScreenRoot(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToJourney: () -> Unit,
    viewModel: OnboardingViewModel
) {
    viewModel.logEvent("splash_screen")
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    Timber.e("@@@@@@@@@@@@@@@@@@ state   ")

    /**
    *
    * accessToken == null일 경우 → 로그인 화면
    * isOnboardingCompleted == false → 온보딩 초기 화면
    * isOnboardingCompleted == true && isNicknameSet == true → 홈화면
    * isOnboardingCompleted == true && nicknameSet == false → 여정일 화면 (온보딩 마지막 화면)
    *
    * */

    LaunchedEffect(state) {
        delay(2000)
        if (state.value.accessToken == null) {
            Timber.e("@@@@@@@@@@@@@@@@@@@@ accessToken null1111")
            onNavigateToLogin()
        } else if (state.value.isOnboardingCompleted == false) {
            Timber.e("@@@@@@@@@@@@@@@@@@@@ accessToken null2222"+state.value.accessToken)
            onNavigateToOnboarding()
        } else if (state.value.isOnboardingCompleted == true && state.value.isNicknameSet == true) {
            Timber.e("@@@@@@@@@@@@@@@@@@@@ accessToken null3333")
            onNavigateToHome()
        } else if (state.value.isOnboardingCompleted == true && state.value.isNicknameSet == false) {
            Timber.e("@@@@@@@@@@@@@@@@@@@@ accessToken null4444")
            onNavigateToJourney()
        }

    }

    SplashScreen()
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ForDayTheme.color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_forday),
            contentDescription = "포데이 로고",
//            modifier = Modifier.padding(bottom = 79.8.dp),
            contentScale = ContentScale.Fit
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    ForDayTheme {
        SplashScreen()
    }
}