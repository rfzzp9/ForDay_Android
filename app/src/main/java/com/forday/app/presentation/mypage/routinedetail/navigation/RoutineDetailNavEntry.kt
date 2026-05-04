package com.forday.app.presentation.mypage.routinedetail.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.core.navigation.MyPage
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.mypage.MyPageViewModel
import com.forday.app.presentation.mypage.navigation.UserPage
import com.forday.app.presentation.mypage.routinedetail.screen.ActivityCardData
import com.forday.app.presentation.mypage.routinedetail.screen.RoutineDetailScreen
import com.forday.app.presentation.mypage.routinedetail.screen.SaveCardScreen
import com.forday.app.presentation.record.navigation.RecordRoutine
import com.forday.app.presentation.sosik.navigation.Register

fun EntryProviderScope<NavKey>.routineDetailNavEntry(
    navigator: Navigator,
    myPageViewModel: MyPageViewModel,
) {
    nonTabEntry<RoutineDetail> { backStackEntry ->
        RoutineDetailScreen(
            viewModel = myPageViewModel,
            routineId = backStackEntry.routineId,
            onBackClick = { navigator.goBack() },
            swipeContext = backStackEntry.swipeContext,
            swipeUserId = backStackEntry.swipeUserId,
            swipeHobbyIds = backStackEntry.swipeHobbyIds,
            notificationId = backStackEntry.notificationId,
            onNavigateToMyPage = { navigator.resetTo(MyPage) },
            isNewRecord = backStackEntry.isNewRecord,
            isUserPageEntry = backStackEntry.isUserPageEntry,
            onNavigateToRecordRoutine = { data, mode -> navigator.navigate(RecordRoutine(modifyData = data, modifyMode = mode)) },
            onNavigateToHome = { navigator.resetTo(com.forday.app.presentation.home.navigation.Home) },
            onNavigateToUserPage = { userId, recordAuthor -> navigator.navigate(UserPage(userId, recordAuthor)) },
            onReportClick = { navigator.navigate(Register()) },
            onSaveCardClick = {
                val details = myPageViewModel.uiState.value.myRoutineDetails
                navigator.navigate(
                    SaveCard(
                        imageUrl = details?.imageUrl ?: "",
                        title = details?.content ?: "",
                        dateTime = details?.date ?: "",
                        memo = details?.memo ?: "",
                        stickerUrl = details?.stickerUrl ?: ""
                    )
                )
            },
        )
    }
}

fun EntryProviderScope<NavKey>.saveCardNavEntry(
    navigator: Navigator,
) {
    nonTabEntry<SaveCard> { backStackEntry ->
        SaveCardScreen(
            cardData = ActivityCardData(
                imageUrl = backStackEntry.imageUrl,
                title = backStackEntry.title,
                dateTime = backStackEntry.dateTime,
                memo = backStackEntry.memo,
                dateFormatted = backStackEntry.dateFormatted,
                stickerUrl = backStackEntry.stickerUrl
            ),
            onBack = { navigator.goBack() },
        )
    }
}
