package com.forday.app.presentation.record.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.hilt.navigation.compose.hiltViewModel
import com.forday.app.core.navigation.MyPage
import com.forday.app.presentation.inputhobbyroutines.navigation.InputRoutine
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail
import com.forday.app.presentation.onboarding.hobbyselect.navigation.SelectHobbyFromModify
import com.forday.app.presentation.record.RecordRoutineViewModel
import com.forday.app.presentation.record.screen.RecordRoutineRoute
import timber.log.Timber

fun EntryProviderScope<NavKey>.recordRoutineNavEntry(
    navigator: Navigator,
    navigationState: MainNavigationState,
) {
    nonTabEntry<RecordRoutine> { backStackEntry ->
        val recordRoutineViewModel: RecordRoutineViewModel = hiltViewModel()
        val hobbyId = backStackEntry.hobbyId
        val modifyData = backStackEntry.modifyData
        val modifyMode = backStackEntry.modifyMode
        val shouldResetToMyPage = backStackEntry.shouldResetToMyPage
        val entryPoint = backStackEntry.entryPoint
        val hobbyName = backStackEntry.hobbyName
        val activityName = backStackEntry.activityName

        RecordRoutineRoute(
            hobbyId = hobbyId,
            modifyData = modifyData,
            modifyMode = modifyMode,
            onComplete = { routineId ->
                Timber.e("@#@#@#@#@##@#routineId $routineId")
                if (shouldResetToMyPage) {
                    navigator.resetTo(MyPage)
                } else {
                    navigator.goBack()
                    navigator.goBack()
                }
                navigator.navigate(RoutineDetail(routineId, !modifyMode))
                Timber.e("routineId@@@@@@@@@@@@@ : $routineId")
            },
            onClose = { navigator.goBack() },
            viewModel = recordRoutineViewModel,
            onRoutineCreate = {
                navigator.goBack()
                navigator.navigate(InputRoutine(hobbyId, null))
            },
            onAddHobbyClick = {
                navigator.goBack()
                navigator.navigate(SelectHobbyFromModify)
            },
            entryPoint = entryPoint,
            hobbyName = hobbyName,
            activityName = activityName
        )
    }
}
