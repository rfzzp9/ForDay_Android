package com.forday.app.presentation.sosik.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.MainTabScaffold
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.toNavKey
import com.forday.app.presentation.main.toBottomBarTab
import com.forday.app.presentation.mypage.navigation.UserPage
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.notification.navigation.Notification
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobbyFromModify
import com.forday.app.presentation.record.navigation.RecordRoutine
import com.forday.app.presentation.sosik.screen.SosikRoute

fun EntryProviderScope<NavKey>.sosikNavEntry(
    navigator: Navigator,
    navigationState: MainNavigationState,
    isRecordedToday: Boolean,
    currentHobbyId: Long?,
    currentHobbyName: String?,
    currentActivityName: String?,
    sosikTabVisitCount: Int,
    onShowAlreadyRecordedDialog: () -> Unit,
) {
    entry<Sosik> {
        MainTabScaffold(
            navigationState = navigationState,
            onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
            onRecordClick = {
                if (isRecordedToday) onShowAlreadyRecordedDialog()
                else navigator.navigate(RecordRoutine(currentHobbyId, entryPoint = "gnb_record", hobbyName = currentHobbyName, activityName = currentActivityName))
            },
        ) { padding ->
            SosikRoute(
                modifier = Modifier.padding(padding),
                tabVisitCount = sosikTabVisitCount,
                onAddHobbyClick = { navigator.navigate(SelectHobbyFromModify) },
                onNotificationClick = { navigator.navigate(Notification) },
                onCardClick = { recordId, isAllTab, hobbyId ->
                    navigator.navigate(
                        RoutineDetail(
                            routineId = recordId,
                            isUserPageEntry = true,
                            swipeContext = if (isAllTab) "STORY_ALL" else "STORY_HOBBY",
                            swipeHobbyIds = hobbyId?.toString()
                        )
                    )
                },
                onProfileClick = { userId, recordAuthor -> navigator.navigate(UserPage(userId, recordAuthor)) }
            )
        }
    }
}
