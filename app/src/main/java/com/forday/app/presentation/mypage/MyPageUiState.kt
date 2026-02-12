package com.forday.app.presentation.mypage

import com.forday.app.presentation.model.ModifyPostingUiModel
import com.forday.app.presentation.mypage.main.FeedContainerUiModel
import com.forday.app.presentation.mypage.main.ScrapListUiModel
import com.forday.app.presentation.mypage.main.UserHobbyTabUiModel
import com.forday.app.presentation.mypage.main.UserInfoUiModel
import com.forday.app.presentation.mypage.routinedetail.HobbyMainImageUiModel
import com.forday.app.presentation.mypage.routinedetail.ReactionDetailUiModel
import com.forday.app.presentation.mypage.routinedetail.RoutineRecordDetailUiModel

data class MyPageUiState(
    val myRoutineDetails: RoutineRecordDetailUiModel? = RoutineRecordDetailUiModel(),  //내 기록 상세보기 모델
    val userInfo: UserInfoUiModel? = null,  // 사용자 정보 조회 모델
    val userHobbyTabUiModel: UserHobbyTabUiModel? = null, // 사용자 취미 진행 상단탭 조회 모델
    val userFeedUiModel: FeedContainerUiModel? = null,  // 사용자 피드 목록 조회 모델
    // 2. 이미지 업로드 관련 상태 (Presigned URL 및 업로드 진행률)
    val imageUploadState: PresignedUrlUiModel = PresignedUrlUiModel(),
    val hobbyMainImageUiModel: HobbyMainImageUiModel = HobbyMainImageUiModel(),
    val reactionUsers: ReactionDetailUiModel = ReactionDetailUiModel(),
    val socialType: String? = null,
    val isKakaoLoginSuccess: Boolean? = null,
    val modifyPostingUiModel: ModifyPostingUiModel = ModifyPostingUiModel(),
    val deletePostingSuccess: Boolean = false,  //활동기록 삭제 성공 여부
    val scrapListUiModel: ScrapListUiModel = ScrapListUiModel(),  // 사용자 스크랩 목록
    val scrapCount: Int? = 0,  // 스크랩 카운트 (임시방편. 수정하기)
    val hasShownGuestBottomSheet: Boolean = false,  // 게스트 체크 - 최초 접속 시에만 바텀 시트 표시
    val isScraped: Boolean?= null,  //활동기록 상세보기에서 북마크 여부
    val isRefreshing: Boolean = false  // Pull-to-Refresh 상태
)