package com.forday.app.presentation.mypage.hobbyphotosetting.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.mypage.MyPageViewModel
import com.forday.app.presentation.mypage.hobbyphotosetting.HobbyPhotoManagementRoute

fun EntryProviderScope<NavKey>.hobbyPhotoSettingNavEntry(
    navigator: Navigator,
    myPageViewModel: MyPageViewModel,
) {
    nonTabEntry<HobbyPhotoSetting> {
        HobbyPhotoManagementRoute(
            viewModel = myPageViewModel,
            onBackClick = { navigator.goBack() },
            onCompleteClick = { navigator.goBack() }
        )
    }
}
