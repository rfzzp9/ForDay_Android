package com.forday.app.presentation.mypage

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.domain.usecase.CancelMyReactionUseCase
import com.forday.app.domain.usecase.CancelScrapPostingUseCase
import com.forday.app.domain.usecase.DeletePostingUseCase
import com.forday.app.domain.usecase.DeleteS3ImageUseCase
import com.forday.app.domain.usecase.GetMyRoutineRecordDetailUseCase
import com.forday.app.domain.usecase.GetPresignedUrlUseCase
import com.forday.app.domain.usecase.GetReactionUsersUseCase
import com.forday.app.domain.usecase.GetUserFeedListUseCase
import com.forday.app.domain.usecase.GetUserInfoUseCase
import com.forday.app.domain.usecase.GetUserNicknameUseCase
import com.forday.app.domain.usecase.GetUserScrapListUseCase
import com.forday.app.domain.usecase.GetUsersProgressHobbyTabsUseCase
import com.forday.app.domain.usecase.ModifyPostingVisibilityUseCase
import com.forday.app.domain.usecase.ReactionToRoutinePostingUseCase
import com.forday.app.domain.usecase.ScrapPostingUseCase
import com.forday.app.domain.usecase.SetHobbyMainImageUseCase
import com.forday.app.domain.usecase.SetProfileImageUseCase
import com.forday.app.domain.usecase.SwitchAccountUseCase
import com.forday.app.domain.usecase.UploadImageToS3UseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.mypage.main.FeedContainerUiModel
import com.forday.app.presentation.mypage.main.UserInfoUiModel
import com.forday.app.core.util.UserMessageCategory
import com.forday.app.core.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import com.forday.app.presentation.mypage.main.toPresentation
import com.forday.app.presentation.mypage.routinedetail.toPresentation
import com.forday.app.presentation.mypage.routinedetail.toUiModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getMyRoutineRecordDetailUseCase: GetMyRoutineRecordDetailUseCase,
    private val reactionToRoutinePostingUseCase: ReactionToRoutinePostingUseCase,  // 활동 기록에 반응 남기기
    private val cancelMyReactionUseCase: CancelMyReactionUseCase,
    private val modifyPostingVisibilityUseCase: ModifyPostingVisibilityUseCase,
    private val getReactionUsersUseCase: GetReactionUsersUseCase,  // 활동 기록에 새로 반응한 사용자 목록 조회
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val setProfileImageUseCase: SetProfileImageUseCase,
    private val getUsersProgressHobbyTabsUseCase: GetUsersProgressHobbyTabsUseCase,  // 사용자 취미 진행 상단탭 조회
    private val getUserFeedListUseCase: GetUserFeedListUseCase,  // 사용자 피드 목록 조회
    private val getPresignedUrlUseCase: GetPresignedUrlUseCase,  // 이미지 업로드용 Presigned URL 발급
    private val uploadImageToS3UseCase: UploadImageToS3UseCase, // S3에 이미지 업로드
    private val deleteS3ImageUseCase: DeleteS3ImageUseCase,   // S3에 등록된 이미지 삭제
    private val getNicknameUseCase: GetUserNicknameUseCase,
    private val setHobbyMainImageUseCase: SetHobbyMainImageUseCase,
    private val switchAccountUseCase: SwitchAccountUseCase,
    private val deletePostingUseCase: DeletePostingUseCase,  // 활동기록 삭제
    private val getUserScrapListUseCase: GetUserScrapListUseCase,  // 스크랩 목록 조회
    private val scrapPostingUseCase: ScrapPostingUseCase,  // 스크랩
    private val cancelScrapPostingUseCase: CancelScrapPostingUseCase,  // 스크랩 취소
    private val getUserData: UserLocalDataSource,   // 나중에 수정 예정 usecase로
    private val snackbarManager: SnackbarManager,
): BaseViewModel<MyPageSideEffect>() {

    private val _uiState: MutableStateFlow<MyPageUiState> = MutableStateFlow(MyPageUiState(
    ))
    val uiState: StateFlow<MyPageUiState> = _uiState.toStateIn()


    fun refresh(selectedTab: Int, selectedHobbyIds: List<Int?>) = viewModelScope.launch {
        _uiState.update { it.copy(isRefreshing = true) }
        try {
            withTimeout(4_000L) {
                val jobs = mutableListOf(
                    launch { getUserInfo().join() },
                    launch { getUsersProgressHobbyTabs().join() },
                    launch {
                        getUserFeedList(
                            hobbyIds = selectedHobbyIds,
                            lastRecordId = null,
                            feedSize = 24
                        ).join()
                    }
                )
                if (selectedTab == 2) {
                    jobs += launch {
                        getUserScrapList(lastScrapId = null, size = 24, userId = null).join()
                    }
                }
                jobs.forEach { it.join() }
            }
        } catch (e: TimeoutCancellationException) {
            e.printStackTrace()
            Timber.d("MyPage refresh timed out after 4s")
        } finally {
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun markGuestBottomSheetShown() {
        _uiState.update { currentState ->
            currentState.copy(hasShownGuestBottomSheet = true)
        }
    }

    fun getMyRoutineRecordDetail(routineId: Int) = viewModelScope.launch {  // 내 활동 기록 상세 조회
        Timber.e("@#@#Calling writeRoutine "+routineId)
        flow {
            emit(getMyRoutineRecordDetailUseCase(routineId.toInt()))
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("@#@@@@@@@@@@@@111 "+throwable)
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update { state ->
                state.copy(
                    myRoutineDetails = data?.toPresentation()
                )
            }
        }
    }

    fun reactionToRoutinePosting(recordId: Int, reactionType: String) = viewModelScope.launch {  // 활동 기록에 반응 남기기
        flow {
            emit(reactionToRoutinePostingUseCase(recordId.toInt(), reactionType))
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("@#@@@@@@@@@@@@111 "+throwable)
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            if (data.status != 200) snackbarManager.show(data.message)
        }
    }

    fun cancelMyReaction(recordId: Int, reactionType: String) = viewModelScope.launch {  // 활동 기록에 반응 취소하기
        flow {
            emit(cancelMyReactionUseCase(recordId, reactionType))
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("@#@@@@@@@@@@@@111 "+throwable)
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            if (data.status != 200) snackbarManager.show(data.message)
        }
    }

    fun modifyPostingVisibility(recordId: Int, visibility: String) = viewModelScope.launch {  // 내 활동 기록 - 공개 범위 수정
        flow {
            emit(modifyPostingVisibilityUseCase(recordId, visibility))
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("@#@@@@@@@@@@@@111 "+throwable)
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            if (data.status != 200) snackbarManager.show(data.message)
        }
    }

    fun deletePosting(recordId: Long) = viewModelScope.launch {
        Timber.e("@#@@@@@@@@@@@@111 deletePosting"+recordId)
        flow {
            // 1. UseCase 호출 (Domain 모델 반환)
            emit(deletePostingUseCase(recordId))
        }.catch { throwable ->
            throwable.printStackTrace()
            // 네트워크 에러나 예상치 못한 예외 처리
            Timber.e("@#@@@@@@@@@@@@111 deletePosting Delete Posting Error $throwable")
            Timber.e("Delete Posting Error: $throwable")
            snackbarManager.show(throwable.toUserMessage())
        }.collect { domainData ->
            // 2. 결과 처리
            if (domainData.isSuccess) {
                Timber.e("@#@@@@@@@@@@@@111 "+domainData.isSuccess)
                _uiState.update {
                    it.copy(
                        deletePostingSuccess = true
                    )
                }
                // 성공 시: 보통 삭제 성공 토스트를 띄우거나 리스트를 새로고침하는 SideEffect를 보냅니다.
//                _sideEffectChannel.send(MyPageSideEffect.DeleteSuccess(domainData.message))

                // 필요하다면 UiState에서 해당 아이템을 즉시 제거하는 로직을 추가할 수 있습니다.
                // updateListAfterDeletion(recordId)
            } else {
                // 실패 시 (404 등): 서버에서 온 에러 메시지를 전달
                snackbarManager.show(domainData.message)
            }
        }
    }

    fun getReactionUsers(recordId: Int, reactionType: String, lastUserId: String, size: Int) = viewModelScope.launch {  // 활동 기록에 새로 반응한 사용자 목록 조회
        flow {
            emit(getReactionUsersUseCase(recordId, reactionType, lastUserId, size))
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("@#@@@@@@@@@@@@111 "+throwable)
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            if (data.status != 200) snackbarManager.show(data.message)
            _uiState.update { state ->
                state.copy(
                    reactionUsers = data.toUiModel()
                )

            }
        }
    }

    fun getUserInfo() = viewModelScope.launch {  // 사용자 정보 조회
        flow {
            emit(getUserInfoUseCase())
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("@#@@@@@@@@@@@@111 "+throwable)
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update { state ->
                state.copy(
                    userInfo = UserInfoUiModel(
                        profileImageUrl = data.imageUrl,
                        nickName = data.nickname,
                        totalCollectedStickerCount = data.stickerCount
                    )
                )
            }
        }
    }

//    fun setProfileImage(imageUrl: String) = viewModelScope.launch {  // 사용자 프로필 이미지 설정
//        flow {
//            emit(setProfileImageUseCase(imageUrl))
//        }.catch { throwable ->
//            Timber.e("@#@@@@@@@@@@@@111 "+throwable)
//            _sideEffectChannel.send(MyPageSideEffect.Exception(throwable))
//        }.collect { data ->
//            if (data.status != 200) _sideEffectChannel.send(MyPageSideEffect.DomainError(data.message))
//        }
//    }

    // MyPageViewModel.kt
    fun setProfileImage(imageUrl: String) = viewModelScope.launch {
        flow {
            emit(setProfileImageUseCase(imageUrl))
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e(throwable, "Failed to set profile image")
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            if (data.status == 200) {
                // 핵심: 서버 업데이트 성공 시 로컬 상태도 동기화
                _uiState.update { state ->
                    state.copy(
                        userInfo = state.userInfo?.copy(
                            profileImageUrl = imageUrl  // "" 또는 새 URL
                        )
                    )
                }

                Timber.d("Profile image updated successfully: $imageUrl")
            } else {
                snackbarManager.show(data.message)
            }
        }
    }

    fun getUsersProgressHobbyTabs() = viewModelScope.launch {  // 사용자 취미 진행 상단탭 조회
        Timber.e("@@@@@@@#################################getUsersProgressHobbyTabs")
        flow {
            emit(getUsersProgressHobbyTabsUseCase())
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("@#@@@@@@@@@@@@111 "+throwable)
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            Timber.e("@#@@@@@@@@@@@@111 "+data)
            _uiState.update { state ->
                state.copy(
                    userHobbyTabUiModel = data.toPresentation()
                )
            }
        }
    }

    fun getUserFeedList(
        hobbyIds: List<Int?>,
        lastRecordId: Long?,
        feedSize: Long?
    ) = viewModelScope.launch {
        flow {
            emit(getUserFeedListUseCase(hobbyIds, lastRecordId, feedSize))
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("getUserFeedList Error: $throwable")
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            Timber.d("getUserFeedList Success: ${data.toPresentation().feedList.size}개 조회")

            _uiState.update { state ->
                val currentFeedList = state.userFeedUiModel?.feedList ?: emptyList()
                val newFeedList = data.toPresentation().feedList

                // lastRecordId가 null이면 새로운 데이터 (처음 로딩 or 필터 변경)
                // lastRecordId가 있으면 기존 데이터에 추가 (무한 스크롤)
                val updatedFeedList = if (lastRecordId == null) {
                    newFeedList  // 새로 시작
                } else {
                    currentFeedList + newFeedList  // 기존에 추가
                }

                val hasMore = newFeedList.isNotEmpty()

                Timber.d("Total feedList size: ${updatedFeedList.size}")

                state.copy(
                    userFeedUiModel = FeedContainerUiModel(
                        feedList = updatedFeedList,
                        lastRecordId = data.lastRecordId,
                        totalFeedCount = data.totalFeedCount,
                        hasMore = hasMore
                    )
                )
            }
        }
    }

    fun getPresignedUrl(images: List<Map<String, Any>>) = viewModelScope.launch {  // 이미지 업로드용 Presigned URL 발급
        images.map { Timber.e("@#@############ "+it) }
        Timber.e("!!!!!!!!!!!!!!!!!getPresignedUrl")
        flow {
            emit(getPresignedUrlUseCase(images).data)
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e(throwable, "@#@############ Failed to get presigned URL   "+throwable)
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            Timber.e("@#@############ Success to get presigned URL"+data)
            _uiState.update { state ->
                state.copy(
                    imageUploadState = data.toPresentation()
                )
            }
        }
    }

    fun deleteProfileImageUi() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                imageUploadState = PresignedUrlUiModel()
            )
        }
    }

    fun deleteS3Image(imageUrl: String) = viewModelScope.launch {
        flow {
            emit(deleteS3ImageUseCase(imageUrl))
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e(throwable, "@#@############ deleteS3Image  throwable : "+throwable)
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            Timber.e("@#@############ deleteS3Image  data : "+data)
            if (data.status == 404) snackbarManager.show(data.data.message)
        }
    }

    // S3에 이미지 업로드
    fun uploadImageToS3(
        file: File,
        uploadUrl: String,
        contentType: String,
        order: Int
    ) = viewModelScope.launch {
        try {
            // 업로드 시작 상태로 업데이트
            updateImageUploadStatus(order, isUploading = true, isSuccess = false)

            Timber.d("Uploading image to S3: ${file.name}, order: $order")

            // S3 업로드 실행
            val result = uploadImageToS3UseCase(
                file = file,
                uploadUrl = uploadUrl,
                contentType = contentType
            )

            result.onSuccess {
                Timber.d("Image upload successful: ${file.name}, order: $order")
                updateImageUploadStatus(order, isUploading = false, isSuccess = true)

            }.onFailure { throwable ->
                Timber.e(throwable, "Image upload failed: ${file.name}, order: $order")
                updateImageUploadStatus(order, isUploading = false, isSuccess = false)
                snackbarManager.show(throwable.toUserMessage())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e, "Unexpected error during upload")
            updateImageUploadStatus(order, isUploading = false, isSuccess = false)
            snackbarManager.show(e.toUserMessage())
        }
    }

    // 특정 이미지의 업로드 상태 업데이트
    private fun updateImageUploadStatus(
        order: Int,
        isUploading: Boolean,
        isSuccess: Boolean
    ) {
        _uiState.update { state ->
            state.copy(
                imageUploadState = state.imageUploadState.copy(
                    isUploading = isUploading,
                    isSuccess = isSuccess
                )
            )
        }
    }

    fun getNickname() = viewModelScope.launch {
        getNicknameUseCase()  // 이미 Flow를 반환하므로 그대로 사용
            .catch { throwable ->
                throwable.printStackTrace()
                snackbarManager.show(throwable.toUserMessage())
            }
            .collect { nickname ->  // nickname은 String? 타입
                _uiState.update { state ->
                    state.copy(
                        userInfo = (state.userInfo ?: UserInfoUiModel()).copy(
                            nickName = nickname
                        )
                    )
                }
            }
    }

    fun getUserScrapList(lastScrapId: Long?, size: Long?, userId: String?) = viewModelScope.launch {
        Timber.e("@#@#@#@#@#@getUserScrapList")
        flow {
            emit(getUserScrapListUseCase(lastScrapId, size, userId))
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("@#@#@#@#@#@getUserScrapList"+throwable)
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            Timber.e("@#@#@#@#@#@getUserScrapList"+data)
            _uiState.update {
                it.copy(
                    scrapListUiModel = data.data.toPresentation()
                )
            }
        }
    }

    fun scrapPosting(routineId: Int) = viewModelScope.launch {  // 스크랩 추가
        flow {
            emit(scrapPostingUseCase(routineId))
        }.catch { throwable ->
            throwable.printStackTrace()
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update {
                it.copy(
                    isScraped = data.data.scraped
                )
            }
            if (data.data.scraped == false) snackbarManager.show(data.data.message)
        }
    }

    fun cancelScrapPosting(routineId: Int) = viewModelScope.launch {  // 스크랩 취소
        flow {
            emit(cancelScrapPostingUseCase(routineId))
        }.catch { throwable ->
            throwable.printStackTrace()
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update {
                it.copy(
                    isScraped = data.isScraped
                )
            }
            if (data.isScraped == true) snackbarManager.show(data.message)
        }
    }

    fun setHobbyMainImage(hobbyId: Long?, imageUrl: String? = null, recordId: Long?) = viewModelScope.launch {  // 취미 대표 이미지 설정
        Timber.e("@@@@@@@@@@@@@@@@@@@@@@@@#@#@#@#@#@# "+imageUrl)
        flow {
            emit(setHobbyMainImageUseCase(hobbyId, imageUrl, recordId))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update { state ->
                state.copy(
                    hobbyMainImageUiModel = data.toPresentation()
                )
            }
        }
    }

    fun getUserLoginInfo() = viewModelScope.launch {
        getUserData.getSocialType().collect { socialType ->
            Timber.e("##########$$$$$$$$$$$$ "+socialType)
            _uiState.update { state ->
                state.copy(
                    socialType = socialType
                )
            }
        }
    }

    fun loginWithKakao(context: Context, socialType: String) {
        val kakao = UserApiClient.instance
        Timber.e("@@@@@@@@@@@@@@1")
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null || token == null) {
                Timber.e("@@@@@@@@@@@@@@2")
                val errorMessage = error?.toUserMessage(UserMessageCategory.AUTH)
                    ?: "로그인에 실패했어요. 잠시 후 다시 시도해주세요."
                sendSideEffect(MyPageSideEffect.DomainError(errorMessage))
            } else {
                Timber.e("@@@@@@@@@@@@@@3")
                loginIntoApp(socialType, token.accessToken)
                Timber.d("token.accessToken ${token.accessToken}")
            }
        }

        if (kakao.isKakaoTalkLoginAvailable(context)) {
            kakao.loginWithKakaoTalk(context) { token, error ->
                if (error != null || token == null) {
                    Timber.e("@@@@@@@@@@@@@@4")
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        sendSideEffect(MyPageSideEffect.DomainError(error.toUserMessage(UserMessageCategory.AUTH)))
                        return@loginWithKakaoTalk
                    }
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else {
                    Timber.e("@@@@@@@@@@@@@@5")
                    loginIntoApp(socialType, token.accessToken)
                    Timber.d("token.accessToken ${token.accessToken}")
                }
            }
        } else {
            Timber.e("@@@@@@@@@@@@@@6")
            kakao.loginWithKakaoAccount(context, callback = callback)
        }
    }

    fun sendSideEffect(sideEffect: MyPageSideEffect) = viewModelScope.launch {
        when (sideEffect) {
            is MyPageSideEffect.DomainError -> snackbarManager.show(sideEffect.error)
            is MyPageSideEffect.Exception -> snackbarManager.show(sideEffect.error.toUserMessage())
        }
    }

    private fun loginIntoApp(socialType: String, kakaoAccessToken: String) = viewModelScope.launch {
        try {
            switchAccountUseCase(socialType, kakaoAccessToken)
                .onSuccess { data ->
                    Timber.e("@@@@@@@@@@@@@@@@@data "+data)
                    _uiState.update {
                        it.copy(
                            isKakaoLoginSuccess = true,
                            socialType = data.socialType
                        )
                    }
                }
                .onFailure { error ->
                    error.printStackTrace()
                    Timber.e("@@@@@@@@@@@@@@@@@ error "+error.message+", "+kakaoAccessToken)
                    val errorMessage = error.toUserMessage(UserMessageCategory.AUTH)
                    _uiState.update {
                        it.copy(
                            isKakaoLoginSuccess = false
                        )
                    }
                    sendSideEffect(MyPageSideEffect.DomainError(errorMessage))
                }
        } catch (e: Exception) { // loginUseCase 호출 자체에서 발생한 예외 처리
            val errorMessage = e.toUserMessage(UserMessageCategory.AUTH)
            _uiState.update {
                it.copy(
                    isKakaoLoginSuccess = false
                )
            }
            sendSideEffect(MyPageSideEffect.DomainError(errorMessage))
        }
    }
//            val imageUrl = if (index in listOf(5, 10, 15, 20)) {
//                "" // 빈 URL
//            } else {
//                "https://picsum.photos/400/480?random=$index"
//            }
//
//            FeedUiModel(
//                recordId = index + 1,
//                url = imageUrl,
//                stickerIconRes = stickerDrawables.random(), // 랜덤으로 선택
//                formattedDate = "2026.01.${27 - index}",
//                memo = m
//            )
//        }
//
//        return FeedContainerUiModel(
//            totalFeedCount = dummyFeedList.size,
//            lastRecordId = dummyFeedList.lastOrNull()?.recordId ?: 0,
//            feedList = dummyFeedList
//        )
//    }

}