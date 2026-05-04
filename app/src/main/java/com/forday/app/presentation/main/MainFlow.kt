package com.forday.app.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.forday.app.core.designsystem.dialog.RoutineOnlyOneHaveDialog
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.core.designsystem.toast.ErrorToast
import com.forday.app.core.navigation.*
import com.forday.app.presentation.allsettings.SettingsSideEffect
import com.forday.app.presentation.allsettings.SettingsViewModel
import com.forday.app.presentation.allsettings.cancelaccount.navigation.cancelAccountNavEntry
import com.forday.app.presentation.allsettings.privacypolicy.navigation.privacyPolicyNavEntry
import com.forday.app.presentation.allsettings.settings.navigation.settingsNavEntry
import com.forday.app.presentation.allsettings.termsofservice.navigation.termsOfServiceNavEntry
import com.forday.app.presentation.common.AppSideEffect
import com.forday.app.presentation.common.SnackbarHostViewModel
import com.forday.app.presentation.discovery.navigation.discoveryNavEntry
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.home.navigation.homeNavEntry
import com.forday.app.presentation.inputhobbyroutines.AiRoutineItemState
import com.forday.app.presentation.inputhobbyroutines.navigation.inputRoutineNavEntry
import com.forday.app.presentation.inputhobbyroutines.navigation.loadingRoutinesNavEntry
import com.forday.app.presentation.inputhobbyroutines.navigation.routineAiRecommendNavEntry
import com.forday.app.presentation.modifyhobby.navigation.modifyHobbyNavEntry
import com.forday.app.presentation.modifyroutine.navigation.modifyRoutineNavEntry
import com.forday.app.presentation.mypage.MyPageViewModel
import com.forday.app.presentation.mypage.hobbyphotosetting.navigation.hobbyPhotoSettingNavEntry
import com.forday.app.presentation.mypage.navigation.myPageNavEntry
import com.forday.app.presentation.mypage.navigation.userPageNavEntry
import com.forday.app.presentation.mypage.profilesetting.navigation.profileSettingNavEntry
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.mypage.routinedetail.navigation.routineDetailNavEntry
import com.forday.app.presentation.mypage.routinedetail.navigation.saveCardNavEntry
import com.forday.app.presentation.notification.navigation.notificationNavEntry
import com.forday.app.presentation.onboarding.OnboardingFlowViewModel
import com.forday.app.presentation.onboarding.OnboardingSideEffect
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.frequencyselect.navigation.selectPerWeekNavEntry
import com.forday.app.presentation.onboarding.hobbyselect.navigation.selectHobbyFromModifyNavEntry
import com.forday.app.presentation.onboarding.hobbyselect.navigation.selectHobbyNavEntry
import com.forday.app.presentation.onboarding.login.navigation.Login
import com.forday.app.presentation.onboarding.login.navigation.loginNavEntry
import com.forday.app.presentation.onboarding.nicknameinput.navigation.inputNicknameNavEntry
import com.forday.app.presentation.onboarding.periodselect.navigation.selectPeriodNavEntry
import com.forday.app.presentation.onboarding.purposeselect.navigation.selectPurposeNavEntry
import com.forday.app.presentation.onboarding.showpobbies.navigation.onboardingSuccessNavEntry
import com.forday.app.presentation.onboarding.showpobbies.navigation.showPobbiesNavEntry
import com.forday.app.presentation.onboarding.splash.SplashViewModel
import com.forday.app.presentation.onboarding.splash.navigation.splashNavEntry
import com.forday.app.presentation.onboarding.swipeintro.navigation.swipeIntroNavEntry
import com.forday.app.presentation.onboarding.termsagreement.navigation.termsAgreementNavEntry
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.forday.app.presentation.onboarding.timeselect.navigation.selectPerTimeNavEntry
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobby
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.frequencyselect.navigation.SelectPerWeek
import com.forday.app.presentation.onboarding.purposeselect.navigation.SelectPurpose
import com.forday.app.presentation.onboarding.timeselect.navigation.SelectPerTime
import com.forday.app.presentation.record.navigation.recordRoutineNavEntry
import com.forday.app.presentation.sosik.navigation.Sosik
import com.forday.app.presentation.sosik.navigation.registerNavEntry
import com.forday.app.presentation.sosik.navigation.sosikNavEntry

import kotlinx.coroutines.delay
import timber.log.Timber
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast as AndroidToast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

@Composable
fun MainFlow(
    initialRoute: NavKey,
    onboardingViewModel: OnboardingViewModel,
    onboardingFlowViewModel: OnboardingFlowViewModel,
    splashViewModel: SplashViewModel,
    deepLinkViewModel: DeepLinkViewModel
) {
    val navigationState = rememberMainNavigationState(
        startRoute = initialRoute,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys as Set<NavKey>
    )

    // SelectPeriod로 시작하는 경우, 이전 온보딩 화면들을 백스택에 미리 채움
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

    val navigator = remember(navigationState) { Navigator(navigationState) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onboardingViewModel.submitConsentWithPermissionResult(isGranted)
    }

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

    var pendingAiRoutine by remember { mutableStateOf<AiRoutineItemState?>(null) }

    val myPageViewModel: MyPageViewModel = hiltViewModel()
    val mainEventViewModel: MainEventViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val snackbarHostViewModel: SnackbarHostViewModel = hiltViewModel()

    val context = LocalContext.current

    // Sosik 탭 전환 감지용 카운터
    var sosikTabVisitCount by remember { mutableIntStateOf(0) }
    val previousTopRoute = remember { mutableStateOf<NavKey?>(null) }

    LaunchedEffect(navigationState.topLevelRoute) {
        val current = navigationState.topLevelRoute
        if (current == Sosik && previousTopRoute.value != null && previousTopRoute.value != Sosik) {
            sosikTabVisitCount++
        }
        previousTopRoute.value = current
    }

    var toastMessage by remember { mutableStateOf<String?>(null) }
    var currentHobbyId by remember { mutableStateOf<Long?>(null) }
    var currentHobbyName by remember { mutableStateOf<String?>(null) }
    var currentActivityName by remember { mutableStateOf<String?>(null) }
    var isRecordedToday by remember { mutableStateOf(false) }
    var todayRecordId by remember { mutableStateOf<Int?>(null) }
    var showAlreadyRecordedDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snackbarHostViewModel.sideEffects.collect { effect ->
            when (effect) {
                is AppSideEffect.ShowSnackbar -> toastMessage = effect.message
            }
        }
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
                            context, Manifest.permission.POST_NOTIFICATIONS
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

    val entryProvider = entryProvider<NavKey> {
        // ── 온보딩 ────────────────────────────────────────────────
        splashNavEntry(splashViewModel = splashViewModel)
        swipeIntroNavEntry(navigator = navigator, onboardingViewModel = onboardingViewModel)
        loginNavEntry(navigator = navigator, onboardingViewModel = onboardingViewModel)
        termsAgreementNavEntry(navigator = navigator, onboardingViewModel = onboardingViewModel)
        selectHobbyNavEntry(navigator = navigator, onboardingFlowViewModel = onboardingFlowViewModel, settingsViewModel = settingsViewModel)
        selectHobbyFromModifyNavEntry(navigator = navigator, onboardingFlowViewModel = onboardingFlowViewModel)
        selectPerTimeNavEntry(navigator = navigator, onboardingFlowViewModel = onboardingFlowViewModel)
        selectPurposeNavEntry(navigator = navigator, onboardingFlowViewModel = onboardingFlowViewModel)
        selectPerWeekNavEntry(navigator = navigator, onboardingFlowViewModel = onboardingFlowViewModel)
        selectPeriodNavEntry(navigator = navigator, onboardingFlowViewModel = onboardingFlowViewModel)
        onboardingSuccessNavEntry(navigator = navigator)
        showPobbiesNavEntry(navigator = navigator, onboardingFlowViewModel = onboardingFlowViewModel)
        inputNicknameNavEntry(navigator = navigator, onboardingFlowViewModel = onboardingFlowViewModel)

        // ── 탭 ───────────────────────────────────────────────────
        homeNavEntry(
            navigator = navigator,
            navigationState = navigationState,
            isRecordedToday = isRecordedToday,
            currentHobbyId = currentHobbyId,
            currentHobbyName = currentHobbyName,
            currentActivityName = currentActivityName,
            onShowAlreadyRecordedDialog = { showAlreadyRecordedDialog = true },
            onCurrentHobbyIdChanged = { currentHobbyId = it },
            onCurrentHobbyInfoChanged = { name, activity -> currentHobbyName = name; currentActivityName = activity },
            onRecordStateChanged = { recorded, recordId -> isRecordedToday = recorded; todayRecordId = recordId },
        )
        discoveryNavEntry(
            navigator = navigator,
            navigationState = navigationState,
            isRecordedToday = isRecordedToday,
            currentHobbyId = currentHobbyId,
            currentHobbyName = currentHobbyName,
            currentActivityName = currentActivityName,
            onShowAlreadyRecordedDialog = { showAlreadyRecordedDialog = true },
        )
        sosikNavEntry(
            navigator = navigator,
            navigationState = navigationState,
            isRecordedToday = isRecordedToday,
            currentHobbyId = currentHobbyId,
            currentHobbyName = currentHobbyName,
            currentActivityName = currentActivityName,
            sosikTabVisitCount = sosikTabVisitCount,
            onShowAlreadyRecordedDialog = { showAlreadyRecordedDialog = true },
        )
        myPageNavEntry(
            navigator = navigator,
            navigationState = navigationState,
            myPageViewModel = myPageViewModel,
            isRecordedToday = isRecordedToday,
            currentHobbyId = currentHobbyId,
            currentHobbyName = currentHobbyName,
            currentActivityName = currentActivityName,
            onShowAlreadyRecordedDialog = { showAlreadyRecordedDialog = true },
        )

        // ── 기능 화면 ─────────────────────────────────────────────
        recordRoutineNavEntry(navigator = navigator, navigationState = navigationState)
        inputRoutineNavEntry(navigator = navigator, navigationState = navigationState, pendingAiRoutine = pendingAiRoutine, onPendingAiRoutineChange = { pendingAiRoutine = it })
        loadingRoutinesNavEntry(navigator = navigator)
        routineAiRecommendNavEntry(navigator = navigator, onPendingAiRoutineChange = { pendingAiRoutine = it })
        modifyRoutineNavEntry(navigator = navigator)
        modifyHobbyNavEntry(navigator = navigator)
        routineDetailNavEntry(navigator = navigator, myPageViewModel = myPageViewModel)
        registerNavEntry(navigator = navigator, myPageViewModel = myPageViewModel)
        saveCardNavEntry(navigator = navigator)
        profileSettingNavEntry(navigator = navigator, myPageViewModel = myPageViewModel)
        hobbyPhotoSettingNavEntry(navigator = navigator, myPageViewModel = myPageViewModel)
        notificationNavEntry(navigator = navigator, settingsViewModel = settingsViewModel)
        settingsNavEntry(navigator = navigator, settingsViewModel = settingsViewModel)
        termsOfServiceNavEntry(navigator = navigator)
        privacyPolicyNavEntry(navigator = navigator)
        cancelAccountNavEntry(navigator = navigator, settingsViewModel = settingsViewModel)
        userPageNavEntry(navigator = navigator)
    }

    ForDayTheme {
        val context = LocalContext.current
        var lastBackPressedAt by remember { mutableLongStateOf(0L) }
        var exitToast by remember { mutableStateOf<AndroidToast?>(null) }

        LaunchedEffect(navigationState.changeId) {
            val activeKeys = navigationState.stacksInUse
            val lastRoutes = activeKeys.associateWith { key ->
                navigationState.backStacks[key]?.lastOrNull()
            }
            Timber.e(
                "NavState(changeId=${navigationState.changeId}) startRoute=${navigationState.startRoute} topLevelRoute=${navigationState.topLevelRoute} stacksInUse=$activeKeys lastRoutes=$lastRoutes"
            )
        }

        BackHandler(enabled = true) {
            val handled = navigator.handleBack()
            if (!handled) {
                if (navigationState.topLevelRoute in TOP_LEVEL_DESTINATIONS.keys && navigationState.topLevelRoute != Home) {
                    navigator.navigate(Home)
                    return@BackHandler
                }
                val now = System.currentTimeMillis()
                if (now - lastBackPressedAt <= 2_000L) {
                    val activity = context.findActivity()
                    exitToast?.view?.animate()?.cancel()
                    val animator = exitToast?.view?.animate()?.alpha(0f)?.setDuration(200L)?.withEndAction {
                        exitToast?.cancel()
                        exitToast = null
                        activity?.finish()
                    }
                    if (animator == null) {
                        exitToast?.cancel()
                        exitToast = null
                        activity?.finish()
                    } else {
                        animator.start()
                    }
                } else {
                    lastBackPressedAt = now
                    exitToast?.view?.animate()?.cancel()
                    exitToast?.cancel()
                    exitToast = AndroidToast.makeText(context, "한 번 더 누르면 종료됩니다", AndroidToast.LENGTH_SHORT).also { toast ->
                        toast.view?.alpha = 0f
                        toast.show()
                        toast.view?.animate()?.alpha(1f)?.setDuration(1000L)?.start()
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Timber.e("@@@@@@@@@@@@@@@@@route: $initialRoute")
            NavDisplay(
                entries = navigationState.toEntries(entryProvider),
                onBack = { navigator.goBack() },
                modifier = Modifier.fillMaxSize(),
            )

            val isTabRootScreen = navigationState.topLevelRoute in TOP_LEVEL_DESTINATIONS.keys &&
                    navigationState.backStacks[navigationState.topLevelRoute]?.lastOrNull() == navigationState.topLevelRoute
            val toastBottomPadding = if (isTabRootScreen) 72.dp else 20.dp

            AnimatedVisibility(
                visible = toastMessage != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = toastBottomPadding)
            ) {
                ErrorToast(message = toastMessage.orEmpty())
            }

            LaunchedEffect(toastMessage) {
                if (toastMessage != null) {
                    delay(2000L)
                    toastMessage = null
                }
            }
        }
    }

    if (showAlreadyRecordedDialog) {
        RoutineOnlyOneHaveDialog(
            onDismiss = { showAlreadyRecordedDialog = false },
            onViewRecords = {
                showAlreadyRecordedDialog = false
                todayRecordId?.let { recordId ->
                    navigator.navigate(RoutineDetail(recordId.toLong()))
                }
            }
        )
    }
}
