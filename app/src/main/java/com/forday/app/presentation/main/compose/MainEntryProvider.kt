package com.forday.app.presentation.main.compose

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.forday.app.presentation.allsettings.SettingsViewModel
import com.forday.app.presentation.allsettings.cancelaccount.navigation.cancelAccountNavEntry
import com.forday.app.presentation.allsettings.privacypolicy.navigation.privacyPolicyNavEntry
import com.forday.app.presentation.allsettings.settings.navigation.settingsNavEntry
import com.forday.app.presentation.allsettings.termsofservice.navigation.termsOfServiceNavEntry
import com.forday.app.presentation.discovery.navigation.discoveryNavEntry
import com.forday.app.presentation.home.navigation.homeNavEntry
import com.forday.app.presentation.inputhobbyroutines.AiRoutineItemState
import com.forday.app.presentation.inputhobbyroutines.navigation.inputRoutineNavEntry
import com.forday.app.presentation.inputhobbyroutines.navigation.loadingRoutinesNavEntry
import com.forday.app.presentation.inputhobbyroutines.navigation.routineAiRecommendNavEntry
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.modifyhobby.navigation.modifyHobbyNavEntry
import com.forday.app.presentation.modifyroutine.navigation.modifyRoutineNavEntry
import com.forday.app.presentation.mypage.MyPageViewModel
import com.forday.app.presentation.mypage.hobbyphotosetting.navigation.hobbyPhotoSettingNavEntry
import com.forday.app.presentation.mypage.navigation.myPageNavEntry
import com.forday.app.presentation.mypage.navigation.userPageNavEntry
import com.forday.app.presentation.mypage.profilesetting.navigation.profileSettingNavEntry
import com.forday.app.presentation.mypage.routinedetail.navigation.routineDetailNavEntry
import com.forday.app.presentation.mypage.routinedetail.navigation.saveCardNavEntry
import com.forday.app.presentation.notification.navigation.notificationNavEntry
import com.forday.app.presentation.onboarding.OnboardingFlowViewModel
import com.forday.app.presentation.onboarding.OnboardingViewModel
import com.forday.app.presentation.onboarding.frequencyselect.navigation.selectPerWeekNavEntry
import com.forday.app.presentation.onboarding.hobbyselect.navigation.selectHobbyFromModifyNavEntry
import com.forday.app.presentation.onboarding.hobbyselect.navigation.selectHobbyNavEntry
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
import com.forday.app.presentation.onboarding.timeselect.navigation.selectPerTimeNavEntry
import com.forday.app.presentation.record.navigation.recordRoutineNavEntry
import com.forday.app.presentation.sosik.navigation.registerNavEntry
import com.forday.app.presentation.sosik.navigation.sosikNavEntry

internal fun mainEntryProvider(
    navigator: Navigator,
    navigationState: MainNavigationState,
    onboardingViewModel: OnboardingViewModel,
    onboardingFlowViewModel: OnboardingFlowViewModel,
    splashViewModel: SplashViewModel,
    settingsViewModel: SettingsViewModel,
    myPageViewModel: MyPageViewModel,
    isRecordedToday: Boolean,
    currentHobbyId: Long?,
    currentHobbyName: String?,
    currentActivityName: String?,
    sosikTabVisitCount: Int,
    pendingAiRoutine: AiRoutineItemState?,
    onShowAlreadyRecordedDialog: () -> Unit,
    onCurrentHobbyIdChanged: (Long?) -> Unit,
    onCurrentHobbyInfoChanged: (String?, String?) -> Unit,
    onRecordStateChanged: (Boolean, Int?) -> Unit,
    onPendingAiRoutineChange: (AiRoutineItemState?) -> Unit,
): (NavKey) -> NavEntry<NavKey> = entryProvider<NavKey> {
    registerOnboardingEntries(
        navigator = navigator,
        onboardingViewModel = onboardingViewModel,
        onboardingFlowViewModel = onboardingFlowViewModel,
        splashViewModel = splashViewModel,
        settingsViewModel = settingsViewModel,
    )
    registerTabEntries(
        navigator = navigator,
        navigationState = navigationState,
        myPageViewModel = myPageViewModel,
        isRecordedToday = isRecordedToday,
        currentHobbyId = currentHobbyId,
        currentHobbyName = currentHobbyName,
        currentActivityName = currentActivityName,
        sosikTabVisitCount = sosikTabVisitCount,
        onShowAlreadyRecordedDialog = onShowAlreadyRecordedDialog,
        onCurrentHobbyIdChanged = onCurrentHobbyIdChanged,
        onCurrentHobbyInfoChanged = onCurrentHobbyInfoChanged,
        onRecordStateChanged = onRecordStateChanged,
    )
    registerFeatureEntries(
        navigator = navigator,
        navigationState = navigationState,
        myPageViewModel = myPageViewModel,
        settingsViewModel = settingsViewModel,
        pendingAiRoutine = pendingAiRoutine,
        onPendingAiRoutineChange = onPendingAiRoutineChange,
    )
}

private fun EntryProviderScope<NavKey>.registerOnboardingEntries(
    navigator: Navigator,
    onboardingViewModel: OnboardingViewModel,
    onboardingFlowViewModel: OnboardingFlowViewModel,
    splashViewModel: SplashViewModel,
    settingsViewModel: SettingsViewModel,
) {
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
}

private fun EntryProviderScope<NavKey>.registerTabEntries(
    navigator: Navigator,
    navigationState: MainNavigationState,
    myPageViewModel: MyPageViewModel,
    isRecordedToday: Boolean,
    currentHobbyId: Long?,
    currentHobbyName: String?,
    currentActivityName: String?,
    sosikTabVisitCount: Int,
    onShowAlreadyRecordedDialog: () -> Unit,
    onCurrentHobbyIdChanged: (Long?) -> Unit,
    onCurrentHobbyInfoChanged: (String?, String?) -> Unit,
    onRecordStateChanged: (Boolean, Int?) -> Unit,
) {
    homeNavEntry(
        navigator = navigator,
        navigationState = navigationState,
        isRecordedToday = isRecordedToday,
        currentHobbyId = currentHobbyId,
        currentHobbyName = currentHobbyName,
        currentActivityName = currentActivityName,
        onShowAlreadyRecordedDialog = onShowAlreadyRecordedDialog,
        onCurrentHobbyIdChanged = onCurrentHobbyIdChanged,
        onCurrentHobbyInfoChanged = onCurrentHobbyInfoChanged,
        onRecordStateChanged = onRecordStateChanged,
    )
    discoveryNavEntry(
        navigator = navigator,
        navigationState = navigationState,
        isRecordedToday = isRecordedToday,
        currentHobbyId = currentHobbyId,
        currentHobbyName = currentHobbyName,
        currentActivityName = currentActivityName,
        onShowAlreadyRecordedDialog = onShowAlreadyRecordedDialog,
    )
    sosikNavEntry(
        navigator = navigator,
        navigationState = navigationState,
        isRecordedToday = isRecordedToday,
        currentHobbyId = currentHobbyId,
        currentHobbyName = currentHobbyName,
        currentActivityName = currentActivityName,
        sosikTabVisitCount = sosikTabVisitCount,
        onShowAlreadyRecordedDialog = onShowAlreadyRecordedDialog,
    )
    myPageNavEntry(
        navigator = navigator,
        navigationState = navigationState,
        myPageViewModel = myPageViewModel,
        isRecordedToday = isRecordedToday,
        currentHobbyId = currentHobbyId,
        currentHobbyName = currentHobbyName,
        currentActivityName = currentActivityName,
        onShowAlreadyRecordedDialog = onShowAlreadyRecordedDialog,
    )
}

private fun EntryProviderScope<NavKey>.registerFeatureEntries(
    navigator: Navigator,
    navigationState: MainNavigationState,
    myPageViewModel: MyPageViewModel,
    settingsViewModel: SettingsViewModel,
    pendingAiRoutine: AiRoutineItemState?,
    onPendingAiRoutineChange: (AiRoutineItemState?) -> Unit,
) {
    recordRoutineNavEntry(navigator = navigator, navigationState = navigationState)
    inputRoutineNavEntry(navigator = navigator, navigationState = navigationState, pendingAiRoutine = pendingAiRoutine, onPendingAiRoutineChange = onPendingAiRoutineChange)
    loadingRoutinesNavEntry(navigator = navigator)
    routineAiRecommendNavEntry(navigator = navigator, onPendingAiRoutineChange = onPendingAiRoutineChange)
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
