package com.forday.app.presentation.modifyhobby

import androidx.lifecycle.viewModelScope
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.core.util.UserMessageCategory
import com.forday.app.core.util.toUserMessage
import com.forday.app.domain.usecase.ChangeHobbyStatusUseCase
import com.forday.app.domain.usecase.GetMyHobbyListUseCase
import com.forday.app.domain.usecase.ModifyHobbyTimeUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ModifyHobbyViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val getMyHobbyListUseCase: GetMyHobbyListUseCase,   // 내 취미 설정 페이지 조회
    private val changeHobbyStatusUseCase: ChangeHobbyStatusUseCase,  // 취미 보관 또는 꺼내기
    private val snackbarManager: SnackbarManager

) : BaseViewModel<ModifyHobbySideEffect>() {

    private val _uiState: MutableStateFlow<ModifyHobbyUiState> = MutableStateFlow(ModifyHobbyUiState())
    val uiState: StateFlow<ModifyHobbyUiState> = _uiState.toStateIn()

    init {
        fetchMyHobbyList(null)  // 처음 조회 시 null로 보내면 진행중인 취미 조회
    }

    fun fetchMyHobbyList(inProgress: String?) = viewModelScope.launch {  // 내 취미 설정 페이지 조회
        _uiState.update { it.copy(isLoading = true) }

        flow {
            emit(getMyHobbyListUseCase(inProgress).data)
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("@@@@@@@@@@@@@@@@@@@"+throwable)
            _sideEffectChannel.send(ModifyHobbySideEffect.Exception(throwable))
        }.collect { data ->
            Timber.e("@@@@@@@@@@@@@@@@@@@data  :::  "+data)
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    currentHobbyStatus = data.currentHobbyStatus,
                    inProgressHobbyCount = data.inProgressHobbyCount,
                    archivedHobbyCount = data.archivedHobbyCount,
                    hobbies = data.hobbies.map { it.toPresentation() }
                )
            }
        }

    }

    fun modifyHobbyStatus(hobbyId: Long, hobbyStatus: String, hobbyName: String) = viewModelScope.launch {
        try {
            val data = changeHobbyStatusUseCase(hobbyId, hobbyStatus)
            Timber.e("@@@@@@@@@@@@@@@@@@@"+data)
            fetchMyHobbyList(_uiState.value.currentHobbyStatus)
            if (hobbyStatus == "ARCHIVED") {
                _uiState.update { it.copy(
                    toastMessage = "'$hobbyName' 취미가 보관되었어요.",
                    toastTargetTab = "ARCHIVED"
                )}
            } else {
                _uiState.update { it.copy(
                    toastMessage = "'$hobbyName' 취미를 꺼냈어요.",
                    toastTargetTab = "IN_PROGRESS"
                )}
            }
        } catch (e: HttpException) {
            val errorClassName = parseErrorClassName(e)
            if (errorClassName == "MAX_IN_PROGRESS_HOBBY_EXCEEDED") {
                Timber.e("@#@#@#@@# $errorClassName")
                _uiState.update { it.copy(showHobbyLimitDialog = true) }
            } else {
                Timber.e("@@@@@@@@@@@@@@@@@@@ $e")
                snackbarManager.show(e.toUserMessage(UserMessageCategory.AUTH))
            }
        } catch (e: Exception) {
            Timber.e("@@@@@@@@@@@@@@@@@@@ $e")
            snackbarManager.show(e.toUserMessage(UserMessageCategory.AUTH))
        }
    }

    private fun parseErrorClassName(e: HttpException): String? {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            val json = Gson().fromJson(errorBody, JsonObject::class.java)
            json?.get("errorClassName")?.asString
                ?: json?.getAsJsonObject("data")?.get("errorClassName")?.asString
        } catch (_: Exception) {
            null
        }
    }

    fun dismissHobbyLimitDialog() {
        _uiState.update { it.copy(showHobbyLimitDialog = false) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null, toastTargetTab = null) }
    }

    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }
}