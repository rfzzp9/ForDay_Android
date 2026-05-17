package com.forday.app.presentation.home.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.designsystem.component.navigationbar.BottomBarTab
import com.forday.app.presentation.home.HomeRoute
import com.forday.app.presentation.home.hobbysetting.navigation.HomeHobbySetting
import com.forday.app.presentation.inputhobbyroutines.navigation.InputRoutine
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.MainTabScaffold
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.toNavKey
import com.forday.app.presentation.main.toBottomBarTab
import com.forday.app.presentation.modifyroutine.navigation.ModifyRoutine
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.notification.navigation.Notification
import com.forday.app.presentation.allsettings.settings.navigation.Settings
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobbyFromModify
import com.forday.app.presentation.record.navigation.RecordRoutine
import timber.log.Timber

fun EntryProviderScope<NavKey>.homeNavEntry(
    navigator: Navigator,
    navigationState: MainNavigationState,
    isRecordedToday: Boolean,
    currentHobbyId: Long?,
    currentHobbyName: String?,
    currentActivityName: String?,
    onShowAlreadyRecordedDialog: () -> Unit,
    onCurrentHobbyIdChanged: (Long?) -> Unit,
    onCurrentHobbyInfoChanged: (String?, String?) -> Unit,
    onRecordStateChanged: (Boolean, Int?) -> Unit,
) {
    entry<Home> {
        MainTabScaffold(
            navigationState = navigationState,
            onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
            onRecordClick = {
                if (isRecordedToday) onShowAlreadyRecordedDialog()
                else navigator.navigate(RecordRoutine(currentHobbyId, entryPoint = "gnb_record", hobbyName = currentHobbyName, activityName = currentActivityName))
            },
        ) { padding ->
            HomeRoute(
                modifier = Modifier.padding(padding),
                onRoutineCreate = { hobbyId, aiCallRemaining, hobbyName ->
                    Timber.e("@@@@@@#######$hobbyId")
                    navigator.navigate(InputRoutine(hobbyId, aiCallRemaining, hobbyName))
                },
                onModifyRoutine = { hobbyId, hobbyName -> navigator.navigate(ModifyRoutine(hobbyId, hobbyName)) },
                onRecordRoutine = { hobbyId, entryPoint, hobbyName, activityName ->
                    Timber.e("@@@@@@#######routineId : $hobbyId")
                    navigator.navigate(RecordRoutine(hobbyId, entryPoint = entryPoint, hobbyName = hobbyName, activityName = activityName))
                },
                onModifyHobby = { navigator.navigate(HomeHobbySetting) },
                onAllSettingsClick = { navigator.navigate(Settings) },
                onNotificationClick = { navigator.navigate(Notification) },
                onMoveRecordedRoutine = { recordId -> navigator.navigate(RoutineDetail(recordId.toLong())) },
                onAddHobbyClick = { navigator.navigate(SelectHobbyFromModify) },
                onSelectHobby = { navigator.navigate(SelectHobbyFromModify) },
                onCurrentHobbyIdChanged = { hobbyId -> onCurrentHobbyIdChanged(hobbyId) },
                onCurrentHobbyInfoChanged = { hobbyName, activityName -> onCurrentHobbyInfoChanged(hobbyName, activityName) },
                onRecordStateChanged = { recorded, recordId -> onRecordStateChanged(recorded, recordId) },
            )
        }
    }
}
