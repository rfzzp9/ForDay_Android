package com.forday.app.presentation.inputhobbyroutines.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.navigation.LoadingRoutines
import com.forday.app.core.navigation.RoutineAiRecommend
import com.forday.app.presentation.inputhobbyroutines.AiRecommendViewModel
import com.forday.app.presentation.inputhobbyroutines.AiRoutineItemState
import com.forday.app.presentation.inputhobbyroutines.InputRoutinesAndAiRecommendViewModel
import com.forday.app.presentation.inputhobbyroutines.LoadingRoutinesViewModel
import com.forday.app.presentation.inputhobbyroutines.screen.AIRecommendationRoutinesRoute
import com.forday.app.presentation.inputhobbyroutines.screen.InputRoutineRoute
import com.forday.app.presentation.inputhobbyroutines.screen.LoadingRoutinesScreen
import com.forday.app.presentation.main.MainNavigationState
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.modifyroutine.navigation.ModifyRoutine

fun EntryProviderScope<NavKey>.inputRoutineNavEntry(
    navigator: Navigator,
    navigationState: MainNavigationState,
    pendingAiRoutine: AiRoutineItemState?,
    onPendingAiRoutineChange: (AiRoutineItemState?) -> Unit,
) {
    nonTabEntry<InputRoutine> { backStackEntry ->
        val hobbyId = backStackEntry.hobbyId
        val hobbyName = backStackEntry.hobbyName
        val inputRoutinesViewModel: InputRoutinesAndAiRecommendViewModel = hiltViewModel()
        InputRoutineRoute(
            hobbyId = hobbyId,
            hobbyName = hobbyName,
            aiCallRemaining = backStackEntry.aiCallRemaining,
            selectedAiRoutine = pendingAiRoutine,
            onAiRoutineConsumed = { onPendingAiRoutineChange(null) },
            onNavigateToModifyRoutine = {
                val stack = navigationState.backStacks[navigationState.topLevelRoute]
                val secondToLast = stack?.let { if (it.size >= 2) it[it.size - 2] else null }
                if (secondToLast is ModifyRoutine) {
                    navigator.goBack()
                } else {
                    navigator.replaceWith(ModifyRoutine(hobbyId = hobbyId, hobbyName = hobbyName))
                }
            },
            onAIRecommendationRoutines = { id -> navigator.navigate(LoadingRoutines(id, hobbyName)) },
            viewModel = inputRoutinesViewModel,
            onExit = { navigator.goBack() }
        )
    }
}

fun EntryProviderScope<NavKey>.loadingRoutinesNavEntry(
    navigator: Navigator,
) {
    nonTabEntry<LoadingRoutines> { backStackEntry ->
        val hobbyId = backStackEntry.hobbyId
        val hobbyName = backStackEntry.hobbyName
        val loadingRoutinesViewModel: LoadingRoutinesViewModel = hiltViewModel()
        val loadingRoutinesState = loadingRoutinesViewModel.uiState.collectAsStateWithLifecycle()
        BackHandler(enabled = true) { }
        LoadingRoutinesScreen(
            hobbyId = hobbyId,
            hobbyName = hobbyName,
            nickname = loadingRoutinesState.value.nickname,
            onNext = { id -> navigator.replaceWith(RoutineAiRecommend(id, hobbyName)) },
            onGetNickname = { loadingRoutinesViewModel.getUserNickname() },
        )
    }
}

fun EntryProviderScope<NavKey>.routineAiRecommendNavEntry(
    navigator: Navigator,
    onPendingAiRoutineChange: (AiRoutineItemState?) -> Unit,
) {
    nonTabEntry<RoutineAiRecommend>(backgroundColor = Color(0xFFF9F9F9)) { backStackEntry ->
        val hobbyId = backStackEntry.hobbyId
        val hobbyName = backStackEntry.hobbyName
        val aiRecommendViewModel: AiRecommendViewModel = hiltViewModel()
        AIRecommendationRoutinesRoute(
            hobbyId = hobbyId,
            hobbyName = hobbyName,
            onBackClick = { navigator.goBack() },
            onNextClick = { selectedRoutine ->
                onPendingAiRoutineChange(selectedRoutine)
                navigator.goBack()
            },
            viewModel = aiRecommendViewModel
        )
    }
}
