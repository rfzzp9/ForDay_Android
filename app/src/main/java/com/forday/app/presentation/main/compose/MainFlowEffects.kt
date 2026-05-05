package com.forday.app.presentation.main.compose

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.allsettings.SettingsSideEffect
import com.forday.app.presentation.allsettings.SettingsViewModel
import com.forday.app.presentation.common.AppSideEffect
import com.forday.app.presentation.common.SnackbarHostViewModel
import com.forday.app.presentation.main.DeepLinkViewModel
import com.forday.app.presentation.main.MainEventViewModel
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.onboarding.OnboardingSideEffect
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.frequencyselect.navigation.SelectPerWeek
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobby
import com.forday.app.presentation.onboarding.login.navigation.Login
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.purposeselect.navigation.SelectPurpose
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.forday.app.presentation.onboarding.timeselect.navigation.SelectPerTime
import com.forday.app.presentation.sosik.navigation.Sosik

@Composable
internal fun PreloadOnboardingBackStackIfNeeded(
    initialRoute: NavKey,
    navigationState: MainNavigationState,
) {
    LaunchedEffect(Unit) {
        if (initialRoute is SelectPeriod) {
            val stack = navigationState.backStacks[initialRoute]
            if (stack != null && stack.size == 1) {
                stack.add(SelectHobby)
                stack.add(SelectPerTime(mode = ScreenMode.ONBOARDING))
                stack.add(SelectPurpose)
                stack.add(SelectPerWeek(mode = ScreenMode.ONBOARDING))
                stack.add(SelectPeriod(mode = ScreenMode.ONBOARDING))
                navigationState.notifyNavChanged()
            }
        }
    }
}

@Composable
internal fun rememberSosikTabVisitCount(
    navigationState: MainNavigationState,
): Int {
    var sosikTabVisitCount by remember { mutableIntStateOf(0) }
    val previousTopRoute = remember { mutableStateOf<NavKey?>(null) }

    LaunchedEffect(navigationState.topLevelRoute) {
        val current = navigationState.topLevelRoute
        if (current == Sosik && previousTopRoute.value != null && previousTopRoute.value != Sosik) {
            sosikTabVisitCount++
        }
        previousTopRoute.value = current
    }

    return sosikTabVisitCount
}

@Composable
internal fun HandlePendingDeepLink(
    deepLinkViewModel: DeepLinkViewModel,
    navigator: Navigator,
) {
    val pendingRecordId by deepLinkViewModel.pendingRecordId.collectAsStateWithLifecycle()
    val pendingNotificationId by deepLinkViewModel.pendingNotificationId.collectAsStateWithLifecycle()

    LaunchedEffect(pendingRecordId) {
        pendingRecordId?.let { recordId ->
            navigator.navigate(
                RoutineDetail(
                    routineId = recordId,
                    swipeContext = "USER_FEED",
                    notificationId = pendingNotificationId
                )
            )
            deepLinkViewModel.consumePendingDeepLink()
        }
    }
}

@Composable
internal fun HandleSnackbarSideEffects(
    snackbarHostViewModel: SnackbarHostViewModel,
    onShowToast: (String) -> Unit,
) {
    LaunchedEffect(Unit) {
        snackbarHostViewModel.sideEffects.collect { effect ->
            when (effect) {
                is AppSideEffect.ShowSnackbar -> onShowToast(effect.message)
            }
        }
    }
}

@Composable
internal fun HandleOnboardingSideEffects(
    onboardingViewModel: OnboardingViewModel,
    snackbarHostViewModel: SnackbarHostViewModel,
    navigator: Navigator,
) {
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onboardingViewModel.submitConsentWithPermissionResult(isGranted)
    }

    LaunchedEffect(Unit) {
        onboardingViewModel.sideEffect.collect { effect ->
            when (effect) {
                is OnboardingSideEffect.GuestAutoReLoginSuccess -> { }
                is OnboardingSideEffect.GuestAutoReLoginFailure -> {
                    snackbarHostViewModel.show("로그인이 만료되었어요. 다시 로그인해주세요.")
                    onboardingViewModel.resetForNewSession()
                    navigator.resetTo(Login)
                }
                is OnboardingSideEffect.TermsConsentSuccess -> {
                    navigator.navigate(SelectHobby)
                }
                is OnboardingSideEffect.RequestNotificationPermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val granted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                        if (granted) onboardingViewModel.submitConsentWithPermissionResult(true)
                        else notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        onboardingViewModel.submitConsentWithPermissionResult(true)
                    }
                }
            }
        }
    }
}

@Composable
internal fun HandleAuthSideEffects(
    mainEventViewModel: MainEventViewModel,
    onboardingViewModel: OnboardingViewModel,
    snackbarHostViewModel: SnackbarHostViewModel,
    navigator: Navigator,
) {
    LaunchedEffect(Unit) {
        mainEventViewModel.authEvents.collect { event ->
            when (event) {
                com.forday.app.core.session.AuthEvent.Expired -> {
                    if (onboardingViewModel.uiState.value.socialType == "GUEST") {
                        onboardingViewModel.guestAutoReLogin()
                    } else {
                        snackbarHostViewModel.show("로그인이 만료되었어요. 다시 로그인해주세요.")
                        onboardingViewModel.resetForNewSession()
                        navigator.resetTo(Login)
                    }
                }
            }
        }
    }
}

@Composable
internal fun HandleSettingsSideEffects(
    settingsViewModel: SettingsViewModel,
    onboardingViewModel: OnboardingViewModel,
    navigator: Navigator,
) {
    LaunchedEffect(Unit) {
        settingsViewModel.sideEffect.collect { effect ->
            when (effect) {
                is SettingsSideEffect.LoggedOut,
                is SettingsSideEffect.AccountCancelled -> {
                    onboardingViewModel.resetForNewSession()
                    navigator.resetTo(Login)
                }
                else -> Unit
            }
        }
    }
}
