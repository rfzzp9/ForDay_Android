package com.forday.app.presentation.mypage.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.allsettings.settings.navigation.Settings
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.mypage.MyPageViewModel
import com.forday.app.presentation.mypage.main.MyPageRoute
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.notification.navigation.Notification
import com.forday.app.presentation.sosik.navigation.Register
import com.forday.app.presentation.sosik.navigation.Sosik
import timber.log.Timber

fun EntryProviderScope<NavKey>.userPageNavEntry(
    navigator: Navigator,
) {
    nonTabEntry<UserPage> { backStackEntry ->
        val userId = backStackEntry.userId
        val recordAuthor = backStackEntry.recordAuthor
        val userPageViewModel: MyPageViewModel = hiltViewModel()
        Timber.e("@@@@@@@@@@@@@@@@@@@@@@ userId : $userId")
        MyPageRoute(
            viewModel = userPageViewModel,
            onProfileSetting = { },
            onHobbyPhotoManagement = { },
            onAllSettingsClick = { navigator.navigate(Settings) },
            onNotificationClick = { navigator.navigate(Notification) },
            onRoutineFeedClick = { recordId, selectedHobbyIds ->
                navigator.navigate(
                    RoutineDetail(
                        routineId = recordId.toLong(),
                        isUserPageEntry = true,
                        swipeContext = "USER_FEED",
                        swipeUserId = userId,
                        swipeHobbyIds = selectedHobbyIds.filterNotNull().joinToString(",").ifEmpty { null }
                    )
                )
            },
            onScrapItemClick = { recordId ->
                navigator.navigate(
                    RoutineDetail(routineId = recordId.toLong(), isUserPageEntry = true, swipeContext = "USER_SCRAP", swipeUserId = userId)
                )
            },
            onAddHobbyClick = { },
            onNavigateToRecordRoutine = { },
            onDismiss = { },
            onBackClick = { navigator.goBack() },
            onNavigateToSosik = { navigator.resetTo(Sosik) },
            onReportUserClick = { navigator.navigate(Register(userId = userId)) },
            userId = userId,
            recordAuthor = recordAuthor,
            isUserPageEntry = true,
        )
    }
}
