package com.forday.app.presentation.mypage.routinedetail.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class RoutineDetail(
    val routineId: Long,
    val isNewRecord: Boolean = false,
    val isUserPageEntry: Boolean = false,
    val swipeContext: String? = null,       // STORY_ALL, STORY_HOBBY, USER_FEED, USER_SCRAP
    val swipeUserId: String? = null,
    val swipeHobbyIds: String? = null,      // 콤마 구분 hobbyId 목록 (예: "1,2,3")
    val notificationId: Long? = null
) : NavKey