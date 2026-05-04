package com.forday.app.presentation.sosik.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.mypage.MyPageViewModel
import com.forday.app.presentation.sosik.screen.ReportRoute

fun EntryProviderScope<NavKey>.registerNavEntry(
    navigator: Navigator,
    myPageViewModel: MyPageViewModel,
) {
    nonTabEntry<Register> { backStackEntry ->
        ReportRoute(
            recordId = myPageViewModel.uiState.value.myRoutineDetails?.recordId ?: 0,
            nickname = myPageViewModel.uiState.value.myRoutineDetails?.writerNickname ?: "",
            onBack = { navigator.goBack() },
            onComplete = { navigator.goBack() },
            onNavigateToSosik = { navigator.resetTo(Sosik) },
            viewModel = myPageViewModel,
            userId = backStackEntry.userId,
        )
    }
}
