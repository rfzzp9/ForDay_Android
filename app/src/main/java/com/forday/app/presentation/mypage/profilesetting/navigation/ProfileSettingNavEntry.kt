package com.forday.app.presentation.mypage.profilesetting.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.forday.app.presentation.main.Navigator
import com.forday.app.presentation.main.nonTabEntry
import com.forday.app.presentation.mypage.MyPageViewModel
import com.forday.app.presentation.mypage.profilesetting.ProfileSettingRoute

fun EntryProviderScope<NavKey>.profileSettingNavEntry(
    navigator: Navigator,
    myPageViewModel: MyPageViewModel,
) {
    nonTabEntry<ProfileSetting> {
        ProfileSettingRoute(
            viewModel = myPageViewModel,
            goBack = { navigator.goBack() },
        )
    }
}
