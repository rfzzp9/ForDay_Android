package com.forday.app.presentation.main.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.allsettings.SettingsViewModel
import com.forday.app.presentation.common.SnackbarHostViewModel
import com.forday.app.presentation.inputhobbyroutines.AiRoutineItemState
import com.forday.app.presentation.main.DeepLinkViewModel
import com.forday.app.presentation.main.MainEventViewModel
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.TOP_LEVEL_DESTINATIONS
import com.forday.app.presentation.main.component.AlreadyRecordedDialogHost
import com.forday.app.presentation.main.rememberMainNavigationState
import com.forday.app.presentation.mypage.MyPageViewModel
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.onboarding.OnboardingFlowViewModel
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.splash.SplashViewModel

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
    PreloadOnboardingBackStackIfNeeded(
        initialRoute = initialRoute,
        navigationState = navigationState,
    )

    val navigator = remember(navigationState) { Navigator(navigationState) }

    var pendingAiRoutine by remember { mutableStateOf<AiRoutineItemState?>(null) }
    val sosikTabVisitCount = rememberSosikTabVisitCount(navigationState)

    val myPageViewModel: MyPageViewModel = hiltViewModel()
    val mainEventViewModel: MainEventViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val snackbarHostViewModel: SnackbarHostViewModel = hiltViewModel()

    var toastMessage by remember { mutableStateOf<String?>(null) }
    var currentHobbyId by remember { mutableStateOf<Long?>(null) }
    var currentHobbyName by remember { mutableStateOf<String?>(null) }
    var currentActivityName by remember { mutableStateOf<String?>(null) }
    var isRecordedToday by remember { mutableStateOf(false) }
    var todayRecordId by remember { mutableStateOf<Int?>(null) }
    var showAlreadyRecordedDialog by remember { mutableStateOf(false) }

    HandlePendingDeepLink(
        deepLinkViewModel = deepLinkViewModel,
        navigator = navigator,
    )
    HandleSnackbarSideEffects(
        snackbarHostViewModel = snackbarHostViewModel,
        onShowToast = { toastMessage = it },
    )
    HandleOnboardingSideEffects(
        onboardingViewModel = onboardingViewModel,
        snackbarHostViewModel = snackbarHostViewModel,
        navigator = navigator,
    )
    HandleAuthSideEffects(
        mainEventViewModel = mainEventViewModel,
        onboardingViewModel = onboardingViewModel,
        snackbarHostViewModel = snackbarHostViewModel,
        navigator = navigator,
    )
    HandleSettingsSideEffects(
        settingsViewModel = settingsViewModel,
        onboardingViewModel = onboardingViewModel,
        navigator = navigator,
    )

    val entryProvider = mainEntryProvider(
        navigator = navigator,
        navigationState = navigationState,
        onboardingViewModel = onboardingViewModel,
        onboardingFlowViewModel = onboardingFlowViewModel,
        splashViewModel = splashViewModel,
        settingsViewModel = settingsViewModel,
        myPageViewModel = myPageViewModel,
        isRecordedToday = isRecordedToday,
        currentHobbyId = currentHobbyId,
        currentHobbyName = currentHobbyName,
        currentActivityName = currentActivityName,
        sosikTabVisitCount = sosikTabVisitCount,
        pendingAiRoutine = pendingAiRoutine,
        onShowAlreadyRecordedDialog = { showAlreadyRecordedDialog = true },
        onCurrentHobbyIdChanged = { currentHobbyId = it },
        onCurrentHobbyInfoChanged = { name, activity ->
            currentHobbyName = name
            currentActivityName = activity
        },
        onRecordStateChanged = { recorded, recordId ->
            isRecordedToday = recorded
            todayRecordId = recordId
        },
        onPendingAiRoutineChange = { pendingAiRoutine = it },
    )

    MainFlowContent(
        initialRoute = initialRoute,
        navigationState = navigationState,
        navigator = navigator,
        entryProvider = entryProvider,
        toastMessage = toastMessage,
        onToastMessageConsumed = { toastMessage = null },
    )

    AlreadyRecordedDialogHost(
        visible = showAlreadyRecordedDialog,
        todayRecordId = todayRecordId,
        onDismiss = { showAlreadyRecordedDialog = false },
        onViewRecords = { recordId ->
            navigator.navigate(RoutineDetail(recordId.toLong()))
        },
    )
}
