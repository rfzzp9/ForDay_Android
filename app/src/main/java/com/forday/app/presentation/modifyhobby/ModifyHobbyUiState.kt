package com.forday.app.presentation.modifyhobby

import com.forday.app.domain.model.MyHobbyListDataDomain
import com.forday.app.domain.model.MyHobbyListItemDomain

// UI 아이템 모델 (이름 변경)
data class HobbyItemUiModel(
    val hobbyId: Int,
    val hobbyName: String,
    val hobbyTimeMinutes: Int,
    val executionCount: Int,
    val goalDays: Int
)

// UI 전체 상태
data class ModifyHobbyUiState(
    val isLoading: Boolean = false,
    val error: String = "",
    val currentHobbyStatus: String? = null,  // 현재 선택된 취미의 상태
    val inProgressHobbyCount: Int = 0,       // 진행 중(IN_PROGRESS) 상태의 취미 개수
    val archivedHobbyCount: Int = 0,         // 보관(ARCHIVED) 상태의 취미 개수
    val hobbies: List<HobbyItemUiModel> = emptyList()  // 취미 목록
)

// 매핑 함수들
fun MyHobbyListItemDomain.toPresentation() = HobbyItemUiModel(
    hobbyId = hobbyId,       // 취미 고유 식별자
    hobbyName = hobbyName,   // 취미 이름
    hobbyTimeMinutes = hobbyTimeMinutes,  // 1회 수행 기준 목표 시간(분)
    executionCount = executionCount,      // 해당 취미의 누적 실행 횟수
    goalDays = goalDays                   // 설정된 목표 일수
)

// ViewModel에서 사용될 최종 변환 로직
fun MyHobbyListDataDomain.toPresentation() = ModifyHobbyUiState(
    currentHobbyStatus = currentHobbyStatus,
    inProgressHobbyCount = inProgressHobbyCount,
    archivedHobbyCount = archivedHobbyCount,
    hobbies = hobbies.map { it.toPresentation() }
)