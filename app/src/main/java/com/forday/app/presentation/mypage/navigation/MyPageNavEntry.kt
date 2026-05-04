package com.forday.app.presentation.mypage.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.allsettings.settings.navigation.Settings
import com.forday.app.core.navigation.MyPage
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.MainTabScaffold
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.toNavKey
import com.forday.app.presentation.main.toBottomBarTab
import com.forday.app.presentation.mypage.MyPageViewModel
import com.forday.app.presentation.mypage.hobbyphotosetting.navigation.HobbyPhotoSetting
import com.forday.app.presentation.mypage.main.MyPageRoute
import com.forday.app.presentation.mypage.profilesetting.navigation.ProfileSetting
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.notification.navigation.Notification
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobbyFromModify
import com.forday.app.presentation.record.navigation.RecordRoutine
import com.forday.app.presentation.sosik.navigation.Register

fun EntryProviderScope<NavKey>.myPageNavEntry(
    navigator: Navigator,
    navigationState: MainNavigationState,
    myPageViewModel: MyPageViewModel,
    isRecordedToday: Boolean,
    currentHobbyId: Long?,
    currentHobbyName: String?,
    currentActivityName: String?,
    onShowAlreadyRecordedDialog: () -> Unit,
) {
    entry<com.forday.app.core.navigation.MyPage> {
        MainTabScaffold(
            navigationState = navigationState,
            onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
            onRecordClick = {
                if (isRecordedToday) onShowAlreadyRecordedDialog()
                else navigator.navigate(RecordRoutine(currentHobbyId, entryPoint = "gnb_record", hobbyName = currentHobbyName, activityName = currentActivityName))
            },
        ) { padding ->
            MyPageRoute(
                modifier = Modifier.padding(padding),
                viewModel = myPageViewModel,
                onProfileSetting = { navigator.navigate(ProfileSetting) },
                onHobbyPhotoManagement = { navigator.navigate(HobbyPhotoSetting) },
                onAllSettingsClick = { navigator.navigate(Settings) },
                onNotificationClick = { navigator.navigate(Notification) },
                onRoutineFeedClick = { recordId, selectedHobbyIds ->
                    navigator.navigate(
                        RoutineDetail(
                            routineId = recordId.toLong(),
                            swipeContext = "USER_FEED",
                            swipeHobbyIds = selectedHobbyIds.filterNotNull().joinToString(",").ifEmpty { null }
                        )
                    )
                },
                onScrapItemClick = { recordId ->
                    navigator.navigate(RoutineDetail(routineId = recordId.toLong(), swipeContext = "USER_SCRAP"))
                },
                onAddHobbyClick = { navigator.navigate(SelectHobbyFromModify) },
                onDismiss = { },
                onNavigateToRecordRoutine = { hobbyId -> navigator.navigate(RecordRoutine(hobbyId = hobbyId?.toLong(), entryPoint = "mypage")) },
                onReportUserClick = { navigator.navigate(Register()) },
            )
        }
    }
}
