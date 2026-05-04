package com.forday.app.presentation.notification.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.allsettings.SettingsViewModel
import com.forday.app.presentation.allsettings.settings.navigation.Settings
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.notification.screen.NotificationRoute

fun EntryProviderScope<NavKey>.notificationNavEntry(
    navigator: Navigator,
    settingsViewModel: SettingsViewModel,
) {
    nonTabEntry<Notification> {
        NotificationRoute(
            onBackClick = { navigator.goBack() },
            onSettingsClick = { navigator.navigate(Settings) },
            onNavigateToRecord = { recordId, notificationId ->
                navigator.navigate(
                    RoutineDetail(routineId = recordId, swipeContext = "USER_FEED", notificationId = notificationId)
                )
            },
            onPermissionGranted = { settingsViewModel.togglePostLikeNotification(true) },
        )
    }
}
