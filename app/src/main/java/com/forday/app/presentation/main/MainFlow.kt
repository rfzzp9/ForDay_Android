package com.forday.app.presentation.main

import androidx.activity.compose.BackHandler
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.forday.app.core.designsystem.component.navigationbar.BottomBar
import com.forday.app.core.designsystem.component.navigationbar.BottomBarTab
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.core.navigation.*
import com.forday.app.presentation.discovery.navigation.Discovery
import com.forday.app.presentation.home.HomeScreenRoot
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.inputhobbyroutines.InputRoutinesAndAiRecommendViewModel
import com.forday.app.presentation.inputhobbyroutines.navigation.InputRoutine
import com.forday.app.presentation.inputhobbyroutines.screen.AIRecommendationRoutinesScreenRoot
import com.forday.app.presentation.inputhobbyroutines.screen.InputRoutineScreenRoot
import com.forday.app.presentation.inputhobbyroutines.screen.LoadingRoutinesScreen
import com.forday.app.presentation.modifyhobby.navigation.ModifyHobby
import com.forday.app.presentation.modifyhobby.screen.ModifyHobbyScreenRoot
import com.forday.app.presentation.modifyroutine.navigation.ModifyRoutine
import com.forday.app.presentation.modifyroutine.screen.ModifyRoutineScreenRoot
import com.forday.app.presentation.mypage.MyPageViewModel
import com.forday.app.presentation.mypage.hobbyphotosetting.HobbyPhotoManagementScreenRoot
import com.forday.app.presentation.mypage.hobbyphotosetting.navigation.HobbyPhotoSetting
import com.forday.app.presentation.mypage.main.MyPageScreen
import com.forday.app.presentation.mypage.profilesetting.ProfileSettingScreenRoot
import com.forday.app.presentation.mypage.profilesetting.navigation.ProfileSetting
import com.forday.app.presentation.mypage.routinedetail.RoutineDetailScreen
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.frequencyselect.SelectFrequencyScreenRoot
import com.forday.app.presentation.onboarding.frequencyselect.navigation.SelectPerWeek
import com.forday.app.presentation.onboarding.hobbyselect.SelectHobbyScreenRoot
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobby
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobbyFromModify
import com.forday.app.presentation.onboarding.login.LoginScreenRoot
import com.forday.app.presentation.onboarding.login.navigation.Login
import com.forday.app.presentation.onboarding.nicknameinput.InputNicknameScreenRoot
import com.forday.app.presentation.onboarding.nicknameinput.navigation.InputNickname
import com.forday.app.presentation.onboarding.periodselect.SelectJourneyDaysScreenRoot
import com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod
import com.forday.app.presentation.onboarding.purposeselect.SelectPurposeScreenRoot
import com.forday.app.presentation.onboarding.purposeselect.navigation.SelectPurpose
import com.forday.app.presentation.onboarding.showpobbies.OnboardingSuccessScreen
import com.forday.app.presentation.onboarding.showpobbies.ShowPobbiesScreen
import com.forday.app.presentation.onboarding.showpobbies.navigation.OnboardingSuccess
import com.forday.app.presentation.onboarding.showpobbies.navigation.ShowPobbies
import com.forday.app.presentation.onboarding.timeselect.ScreenMode
import com.forday.app.presentation.onboarding.timeselect.SelectTimeScreenRoot
import com.forday.app.presentation.onboarding.timeselect.navigation.SelectPerTime
import com.forday.app.presentation.record.navigation.RecordRoutine
import com.forday.app.presentation.record.screen.RecordRoutineScreenRoot
import com.forday.app.presentation.story.navigation.Story
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.forday.app.core.session.AuthEvent
import com.forday.app.presentation.allsettings.SettingsViewModel
import com.forday.app.presentation.allsettings.cancelaccount.navigation.CancelAccount
import com.forday.app.presentation.allsettings.cancelaccount.screen.CancelAccountScreen
import com.forday.app.presentation.record.RecordRoutineViewModel
import com.forday.app.presentation.allsettings.settings.navigation.Settings
import com.forday.app.presentation.allsettings.settings.screen.SettingsScreen
import com.forday.app.presentation.allsettings.privacypolicy.navigation.PrivacyPolicy
import com.forday.app.presentation.allsettings.privacypolicy.screen.PrivacyPolicyScreen
import com.forday.app.presentation.allsettings.termsofservice.navigation.TermsOfService
import com.forday.app.presentation.allsettings.termsofservice.screen.TermsOfServiceScreen
import com.forday.app.presentation.common.AppSideEffect
import com.forday.app.presentation.common.SnackbarHostViewModel
import timber.log.Timber

private fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

@Composable
fun MainFlow(initialRoute: NavKey, onboardingViewModel: OnboardingViewModel) {

    val navigationState = rememberMainNavigationState(
        startRoute = initialRoute,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys
    )

    val navigator = remember(navigationState) { Navigator(navigationState) }

    val inputRoutinesAndAiRecommendViewModel: InputRoutinesAndAiRecommendViewModel = hiltViewModel()
    val myPageViewModel: MyPageViewModel = hiltViewModel()
    val mainEventViewModel: MainEventViewModel = hiltViewModel()
    val recordRoutineViewModel: RecordRoutineViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val snackbarHostViewModel: SnackbarHostViewModel = hiltViewModel()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snackbarHostViewModel.sideEffects.collect { effect ->
            when (effect) {
                is AppSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        mainEventViewModel.authEvents.collect { event ->
            when (event) {
                AuthEvent.Expired -> {
                    snackbarHostViewModel.show("로그인이 만료되었어요. 다시 로그인해주세요.")
                    navigator.resetTo(Login)
                }
            }
        }
    }

    val entryProvider = entryProvider<NavKey> {
        entry<Login> {
            LoginScreenRoot(
                onNavigateToHome = {
                    Timber.e("MainFlow(Login) - navigate to Home requested")
                    navigator.navigate(Home)
                },
                onNavigateToOnboarding = {
                    Timber.e("MainFlow(Login) - navigate to SelectHobby requested")
                    navigator.navigate(SelectHobby)
                },
                viewModel = onboardingViewModel
            )
        }

        // ==================== 온보딩 플로우 ====================

        entry<SelectHobby> {
            SelectHobbyScreenRoot(
                onNext = { navigator.navigate(SelectPerTime(mode = ScreenMode.ONBOARDING)) },
                onBack = { navigator.goBack() },
                viewModel = onboardingViewModel
            )
        }

        entry<SelectHobbyFromModify> {
            SelectHobbyScreenRoot(
                onNext = { navigator.navigate(SelectPerTime(mode = ScreenMode.ONBOARDING)) },
                onBack = { navigator.goBack() },
                viewModel = onboardingViewModel,
                fromModifyHobbyOrHome = true
            )
        }

        entry<SelectPerTime> { backStackEntry ->
            SelectTimeScreenRoot(
                params = backStackEntry.params,
                onNext = {
                    if (backStackEntry.mode == ScreenMode.ONBOARDING) navigator.navigate(SelectPerWeek(mode = ScreenMode.ONBOARDING))
                    else navigator.goBack()
                },
                onBack = { navigator.goBack() },
                viewModel = onboardingViewModel,
                mode = backStackEntry.mode
            )
        }

        entry<SelectPurpose> {
            SelectPurposeScreenRoot(
                onNext = { navigator.navigate(SelectPeriod(mode = ScreenMode.ONBOARDING)) },
                onBack = { navigator.goBack() },
                viewModel = onboardingViewModel
            )
        }

        entry<SelectPerWeek> { backStackEntry ->
            SelectFrequencyScreenRoot(
                params = backStackEntry.params,
                onNext = {
                    if (backStackEntry.mode == ScreenMode.ONBOARDING) navigator.navigate(
                        SelectPurpose
                    )
                    else navigator.goBack()
                },
                onBack = { navigator.goBack() },
                viewModel = onboardingViewModel,
                mode = backStackEntry.mode
            )
        }

        entry<SelectPeriod> { backStackEntry ->
            SelectJourneyDaysScreenRoot(
                params = backStackEntry.params,
                mode = backStackEntry.mode,
                onNext = {
                    if (backStackEntry.mode == ScreenMode.ONBOARDING) navigator.navigate(
                        OnboardingSuccess
                    )
                    else navigator.goBack()
                },
                onBack = { navigator.goBack() },
                viewModel = onboardingViewModel,
                goHome = { navigator.resetTo(Home) }
            )
        }

        entry<OnboardingSuccess> {
            BackHandler(enabled = true) { }

            OnboardingSuccessScreen(
                onNext = { navigator.navigate(ShowPobbies) },
                onDirectHome = { navigator.navigate(Home) },
                viewModel = onboardingViewModel
            )
        }

        entry<ShowPobbies> {
            BackHandler(enabled = true) { }

            ShowPobbiesScreen(
                onNext = { navigator.navigate(InputNickname) }
            )
        }

        entry<InputNickname> {
            BackHandler(enabled = true) { }

            InputNicknameScreenRoot(
                onNext = { navigator.resetTo(Home) },
                viewModel = onboardingViewModel
            )
        }

        // ==================== 메인 앱 (Bottom Bar 탭들) ====================

        entry<Home> {
            MainTabScaffold(
                navigationState = navigationState,
                onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
                onRecordClick = { navigator.navigate(RecordRoutine()) },
            ) { padding ->
                HomeScreenRoot(
                    modifier = Modifier.padding(padding),
                    onRoutineCreate = { hobbyId, aiCallRemaining ->
                        Timber.e("@@@@@@#######" + hobbyId)
                        navigator.navigate(InputRoutine(hobbyId, aiCallRemaining))
                    },
                    onModifyRoutine = { hobbyId -> navigator.navigate(ModifyRoutine(hobbyId)) },
                    onRecordRoutine = { hobbyId ->
                        Timber.e("@@@@@@#######routineId : " + hobbyId)
                        navigator.navigate(RecordRoutine(hobbyId))
                    },
                    onModifyHobby = { navigator.navigate(ModifyHobby) },
                    onAllSettingsClick = { navigator.navigate(Settings) },
                    onMoveRecordedRoutine = { recordId ->
                        navigator.navigate(RoutineDetail(recordId.toLong()))
                    },
                    onAddHobbyClick = { navigator.navigate(SelectHobbyFromModify) },
                    onSelectHobby = { navigator.navigate(SelectHobbyFromModify) },
                )
            }
        }

        entry<Discovery> {
            MainTabScaffold(
                navigationState = navigationState,
                onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
                onRecordClick = { navigator.navigate(RecordRoutine()) },
            ) { _ ->
                // DiscoveryScreenRoot()
            }
        }

        entry<Story> {
            MainTabScaffold(
                navigationState = navigationState,
                onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
                onRecordClick = { navigator.navigate(RecordRoutine()) },
            ) { _ ->
                // StoryScreenRoot()
            }
        }

        entry<MyPage> {
            MainTabScaffold(
                navigationState = navigationState,
                onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
                onRecordClick = { navigator.navigate(RecordRoutine()) },
            ) { padding ->
                MyPageScreen(
                    modifier = Modifier.padding(padding),
                    viewModel = myPageViewModel,
                    onProfileSetting = { navigator.navigate(ProfileSetting) },  // 내 프로필 설정으로 이동
                    onHobbyPhotoManagement = { navigator.navigate(HobbyPhotoSetting) },  // 취미 대표사진 관리로 이동
                    onAllSettingsClick = { navigator.navigate(Settings) }, // 전체설정
                    onRoutineFeedClick = { routineId -> navigator.navigate(RoutineDetail(routineId.toLong())) },
                    onAddHobbyClick = { navigator.navigate(SelectHobby) },
                    onDismiss = { },
                    onNavigateToRecordRoutine = { navigator.navigate(RecordRoutine()) },
                )
            }
        }

        // ==================== 기타 화면들 (Bottom Bar 없음) ====================

        entry<RecordRoutine> { backStackEntry ->
            val hobbyId = backStackEntry.hobbyId
            val modifyData = backStackEntry.modifyData
            val modifyMode = backStackEntry.modifyMode

            RecordRoutineScreenRoot(
                hobbyId = hobbyId,
                modifyData = modifyData,
                modifyMode = modifyMode,
                onComplete = {
                        routineId ->
                    Timber.e("@#@#@#@#@##@#routineId "+routineId)
                    navigator.resetTo(MyPage)
                    navigator.navigate(RoutineDetail(routineId, true))
                    Timber.e("routineId@@@@@@@@@@@@@ : "+routineId)
                },
                onClose = { navigator.goBack() },
                viewModel = recordRoutineViewModel
            )
        }

        entry<InputRoutine> { backStackEntry ->
            val hobbyId = backStackEntry.hobbyId
            Timber.e("@@@@@@@########@@@@@@ " + hobbyId)
            InputRoutineScreenRoot(
                hobbyId = hobbyId,
                aiCallRemaining = backStackEntry.aiCallRemaining,
                onCreateRoutines = { navigator.goBack() },  // 이전 탭으로 돌아가기
                onAIRecommendationRoutines = { hobbyId -> navigator.navigate(LoadingRoutines(hobbyId)) },
                viewModel = inputRoutinesAndAiRecommendViewModel,
                onExit = { navigator.goBack() }
            )
        }

        entry<LoadingRoutines> { backStackEntry ->
            val hobbyId = backStackEntry.hobbyId
            BackHandler(enabled = true) { }

            LoadingRoutinesScreen(
                hobbyId = hobbyId,
                onNext = { hobbyId ->
                    navigator.replaceWith(RoutineAiRecommend(hobbyId))
                },
                viewModel = inputRoutinesAndAiRecommendViewModel
            )
        }

        entry<RoutineAiRecommend> { backStackEntry ->
            val hobbyId = backStackEntry.hobbyId
            AIRecommendationRoutinesScreenRoot(
                hobbyId = hobbyId,
                onBackClick = { navigator.goBack() },
                onNextClick = { navigator.goBack() },
                viewModel = inputRoutinesAndAiRecommendViewModel
            )
        }

        entry<ModifyRoutine> { backStackEntry ->
            val hobbyId = backStackEntry.hobbyId
            // ModifyRoutine 화면 구현
            ModifyRoutineScreenRoot(
                hobbyId = hobbyId,
                onBack = { navigator.goBack() },
                onAddRoutine = { navigator.navigate(InputRoutine(hobbyId)) },
                onEditRoutine = {},
            )
        }

        entry<ModifyHobby> {  // 내 취미정보를 수정하고 추가하기
            ModifyHobbyScreenRoot(
                onAddHobby = { navigator.navigate(SelectHobbyFromModify) },   // 취미 추가 (취미 생성)
                onChangeDuration = { params -> navigator.navigate(SelectPerTime(params = params, mode = ScreenMode.DEFAULT)) },
                onChangeFrequency = { params -> navigator.navigate(SelectPerWeek(params = params, mode = ScreenMode.DEFAULT)) },  // 취미횟수
                onChangeJourneyDays = { params -> navigator.navigate(SelectPeriod(params = params, mode = ScreenMode.DEFAULT)) },  // 여정일
                onBack = { navigator.goBack() },
            )
        }

        entry<RoutineDetail> { backStackEntry ->
            val routineId = backStackEntry.routineId
            val isNewRecord = backStackEntry.isNewRecord
            RoutineDetailScreen(
                viewModel = myPageViewModel,
                routineId = routineId,
                onBackClick = {navigator.goBack()},
                onNavigateToMyPage = {
                    navigator.resetTo(MyPage)
                },
                isNewRecord = isNewRecord,
                onNavigateToRecordRoutine = { data, mode -> navigator.navigate(RecordRoutine(modifyData = data, modifyMode = mode)) },
                onNavigateToHome = { navigator.resetTo(Home) },
            )
        }

        entry<ProfileSetting> {
            ProfileSettingScreenRoot(
                viewModel = myPageViewModel,
                goBack = { navigator.goBack() },
            )
        }

        entry<HobbyPhotoSetting> {
            HobbyPhotoManagementScreenRoot(
                viewModel = myPageViewModel,
                onBackClick = { navigator.goBack() },
                onCompleteClick = { navigator.goBack() }
            )
        }

        entry<Settings> {
            SettingsScreen(
                onBackClick = { navigator.goBack() },
                onTermsOfServiceClick = { navigator.navigate(TermsOfService) },
                onPrivacyPolicyClick = { navigator.navigate(PrivacyPolicy) },
                navigateToLogin = { navigator.resetTo(Login) },
                onCancelAccountClick = { navigator.navigate(CancelAccount) },
                viewModel = settingsViewModel
            )
        }

        entry<TermsOfService> {
            TermsOfServiceScreen(
                onCloseClick = { navigator.goBack() }
            )
        }

        entry<PrivacyPolicy> {
            PrivacyPolicyScreen(
                onCloseClick = { navigator.goBack() }
            )
        }

        entry<CancelAccount> {
            CancelAccountScreen(
                onBackClick = { navigator.goBack() },
                onNavigateToLogin = { navigator.resetTo(Login) },
                viewModel = settingsViewModel
            )
        }
    }

    ForDayTheme {
        val context = LocalContext.current
        var lastBackPressedAt by remember { mutableLongStateOf(0L) }
        var exitToast by remember { mutableStateOf<Toast?>(null) }

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
            if (!handled && navigationState.topLevelRoute in TOP_LEVEL_DESTINATIONS.keys) {
                if (navigationState.topLevelRoute != Home) {
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
                    exitToast = Toast.makeText(context, "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).also { toast ->
                        toast.view?.alpha = 0f
                        toast.show()
                        toast.view?.animate()?.alpha(1f)?.setDuration(1000L)?.start()
                    }
                }
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Timber.e("@@@@@@@@@@@@@@@@@route: " + initialRoute)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavDisplay(
                    entries = navigationState.toEntries(entryProvider),
                    onBack = { navigator.goBack() },
                )

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun MainTabScaffold(
    navigationState: MainNavigationState,
    onTabSelected: (BottomBarTab) -> Unit,
    onRecordClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            BottomBar(
                modifier = Modifier,
                selectedTab = navigationState.topLevelRoute.toBottomBarTab(),
                onTabSelected = onTabSelected,
                onRecordClick = onRecordClick,
            )
        },
        content = content,
    )
}