package com.forday.app.presentation.discovery.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.discovery.DiscoveryScreen
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.MainTabScaffold
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.toNavKey
import com.forday.app.presentation.main.toBottomBarTab
import com.forday.app.presentation.record.navigation.RecordRoutine

fun EntryProviderScope<NavKey>.discoveryNavEntry(
    navigator: Navigator,
    navigationState: MainNavigationState,
    isRecordedToday: Boolean,
    currentHobbyId: Long?,
    currentHobbyName: String?,
    currentActivityName: String?,
    onShowAlreadyRecordedDialog: () -> Unit,
) {
    entry<Discovery> {
        MainTabScaffold(
            navigationState = navigationState,
            onTabSelected = { tab -> navigator.navigate(tab.toNavKey()) },
            onRecordClick = {
                if (isRecordedToday) onShowAlreadyRecordedDialog()
                else navigator.navigate(RecordRoutine(currentHobbyId, entryPoint = "gnb_record", hobbyName = currentHobbyName, activityName = currentActivityName))
            },
        ) { _ ->
            DiscoveryScreen()
        }
    }
}
