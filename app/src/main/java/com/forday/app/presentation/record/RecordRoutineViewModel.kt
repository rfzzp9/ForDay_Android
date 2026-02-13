package com.forday.app.presentation.record

import androidx.lifecycle.viewModelScope
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.core.util.toUserMessage
import com.forday.app.domain.usecase.DeleteS3ImageUseCase
import com.forday.app.domain.usecase.GetMyRoutineRecordDetailUseCase
import com.forday.app.domain.usecase.GetPresignedUrlUseCase
import com.forday.app.domain.usecase.GetSpecificRoutineListUseCase
import com.forday.app.domain.usecase.ModifyHobbyRoutineUseCase
import com.forday.app.domain.usecase.ModifyPostingUseCase
import com.forday.app.domain.usecase.UploadImageToS3UseCase
import com.forday.app.domain.usecase.WriteRoutineUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.model.toModifyPostingUiModel
import com.forday.app.presentation.mypage.MyPageSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RecordRoutineViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val modifyHobbyRoutineUseCase: ModifyHobbyRoutineUseCase,  // 수정용 취미활동
    private val getSpecificRoutineListUseCase: GetSpecificRoutineListUseCase,  // 특정 취미의 활동 목록 조회
    private val writeRoutineUseCase: WriteRoutineUseCase,
    private val getPresignedUrlUseCase: GetPresignedUrlUseCase,  // 이미지 업로드용 Presigned URL 발급
    private val uploadImageToS3UseCase: UploadImageToS3UseCase,
    private val modifyPostingUseCase: ModifyPostingUseCase,
    private val deleteS3ImageUseCase: DeleteS3ImageUseCase,   // S3에 등록된 이미지 삭제
    private val getMyRoutineRecordDetailUseCase: GetMyRoutineRecordDetailUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<Unit>() {

    private val _uiState: MutableStateFlow<RecordRoutineUiState> = MutableStateFlow(RecordRoutineUiState())
    val uiState: StateFlow<RecordRoutineUiState> = _uiState.toStateIn()

    fun writeRoutine(
        routineId: Long,
        sticker: String,
        memo: String,
        imageUrl: String,
        visibility: String,
        recordId: Int? = null,
        onSuccess: (Long) -> Unit
    ) = viewModelScope.launch {  //취미활동 기록
        flow {
            emit(writeRoutineUseCase(routineId, sticker, memo, imageUrl, visibility).data.toPresentation())
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("@#@####writeRoutine@#@ "+throwable)
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            Timber.e("@#@####writeRoutine@#@ "+data)

            // ✅ State 업데이트
            _uiState.update { state ->
                state.copy(
                    recordDetail = state.recordDetail.copy(
                        routineRecordId = recordId ?: data.routineRecordId,  //수정모드는 recordId, 신규는 data.routineRecordId
                        routineContent = data.routineContent,
                        stickerUrl = data.stickerUrl,
                        memo = data.memo,
                        imageUrl = data.imageUrl,
                        isExtensionRequired = data.isExtensionRequired,
                        successMessage = data.successMessage
                    )
                )
            }

            // ✅ 콜백 즉시 호출 (지연 없음)
            onSuccess(data.routineRecordId.toLong())
        }
    }

    fun modifyPosting(recordId: Int, routineId: Int, sticker: String, memo: String, imageUrl: String, visibility: String) = viewModelScope.launch {
        Timber.e("@@@@@@@@@@2323@@@@@@@@ memo "+memo)
        flow {
            emit(modifyPostingUseCase(recordId, routineId, sticker, memo, imageUrl, visibility))
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("@@@@@@@@@@2323@@@@@@@@"+throwable)
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update { state ->
                Timber.e("@@@@@@@@@@2323@@@@@@@@"+data.message)
                state.copy(
                    modifyPostingUiModel = data.toModifyPostingUiModel()
                )
            }
        }
    }

    fun resetModifyPostingUiModel() {
        _uiState.update { state ->
            state.copy(modifyPostingUiModel = null)
        }
    }

    fun getMyRoutineRecordDetail(recordId: Int) = viewModelScope.launch {
        flow {
            emit(getMyRoutineRecordDetailUseCase(recordId))
        }.catch { throwable ->
            throwable.printStackTrace()
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            data?.let { detail ->
                _uiState.update { state ->
                    state.copy(
                        recordDetail = state.recordDetail.copy(
                            routineRecordId = detail.recordId,
                            routineContent = detail.content,
                            stickerUrl = detail.sticker,
                            memo = detail.memo,
                            imageUrl = detail.image
                        )
                    )
                }
            }
        }
    }

    fun modifyRoutine(routineId: Long, content: String) = viewModelScope.launch {
        flow {
            emit(modifyHobbyRoutineUseCase(routineId, content))
        }.catch { throwable ->
            throwable.printStackTrace()
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            if (data.status == 200) {
                // ✅ 성공 시 해당 routineId의 content 업데이트
                _uiState.update { currentState ->
                    currentState.copy(
                        routines = currentState.routines.map { routine ->
                            if (routine.routineId.toLong() == routineId) {
                                routine.copy(content = content)
                            } else {
                                routine
                            }
                        }
                    )
                }
            } else {
                snackbarManager.show(data.data.message)
            }
        }
    }

    fun fetchSpecificRoutineList(hobbyId: Long?, size: Int?) = viewModelScope.launch {  // 드롭 다운용 특정 취미 활동 목록 조회
        flow {
            val response = getSpecificRoutineListUseCase(hobbyId, size)
            emit(response.data.routines)
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("@##@#@#@#@#@#@#@@ "+throwable)
            snackbarManager.show(throwable.toUserMessage())
        }.collect { routines ->
            val routineUiModels = routines.map { item ->
                RoutineUiModel(
                    routineId = item.routineId,
                    content = item.content,
                    isAiRecommended = item.aiRecommended,
                )
            }

            _uiState.update { state ->
                state.copy(
                    recordDetail = state.recordDetail.copy(
                        routineList = routineUiModels
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
                    imageUploadState = data.toUiModel()
                )
            }
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

    // ✅ S3에 이미지 업로드
    fun uploadImageToS3(
        file: File,
        uploadUrl: String,
        contentType: String,
        order: Int
    ) = viewModelScope.launch {
        try {
            // 업로드 시작 상태로 업데이트
            updateImageUploadStatus(order, isUploading = true)

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
                throwable.printStackTrace()
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

    // ✅ 특정 이미지의 업로드 상태 업데이트
    private fun updateImageUploadStatus(
        order: Int,
        isUploading: Boolean = false,
        isSuccess: Boolean = false
    ) {
        _uiState.update { state ->
            state.copy(
                imageUploadState = state.imageUploadState.copy(
                    images = state.imageUploadState.images.map { item ->
                        if (item.order == order) {
                            item.copy(
                                isUploading = isUploading,
                                isSuccess = isSuccess
                            )
                        } else {
                            item
                        }
                    }
                )
            )
        }
    }


    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

}