package com.forday.app.presentation.home.hobbysetting

import androidx.lifecycle.viewModelScope
import com.forday.app.domain.model.CreateHobbyItemDomain
import com.forday.app.domain.model.HomeHobbySettingHiddenHobbyRequestDomain
import com.forday.app.domain.model.HomeHobbySettingItemDomain
import com.forday.app.domain.model.HomeHobbySettingProgressHobbyRequestDomain
import com.forday.app.domain.usecase.CreateHobbiesUseCase
import com.forday.app.domain.usecase.DeleteHobbyUseCase
import com.forday.app.domain.usecase.GetHomeHobbySettingListUseCase
import com.forday.app.domain.usecase.UpdateHomeHobbySettingListUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeHobbySettingViewModel @Inject constructor(
    private val getHomeHobbySettingListUseCase: GetHomeHobbySettingListUseCase,
    private val updateHomeHobbySettingListUseCase: UpdateHomeHobbySettingListUseCase,
    private val createHobbiesUseCase: CreateHobbiesUseCase,
    private val deleteHobbyUseCase: DeleteHobbyUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<Unit>() {

    private val _uiState = MutableStateFlow(HomeHobbySettingUiState())
    val uiState: StateFlow<HomeHobbySettingUiState> = _uiState.toStateIn()

    private var originalActiveHobbies: List<HomeHobbySettingItemUiModel> = emptyList()
    private var originalHiddenHobbies: List<HomeHobbySettingItemUiModel> = emptyList()
    private var originalActiveHobbyIds: List<Long> = emptyList()
    private var originalHiddenHobbyIds: List<Long> = emptyList()
    private var pendingActionAfterSave: PendingActionAfterSave? = null

    fun loadHobbies() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, saveCompleted = false) }

        runCatching {
            val data = getHomeHobbySettingListUseCase().data
            val active = data.progressHobbyList.map { it.toUiModel(isActive = true) }
            val hidden = data.hiddenHobbyList.map { it.toUiModel(isActive = false) }
            updateOriginalHobbies(active = active, hidden = hidden)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    activeHobbies = active,
                    hiddenHobbies = hidden,
                    errorMessage = null,
                )
            }
        }.onFailure { throwable ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "취미 설정 목록을 불러오지 못했어요.",
                )
            }
        }
    }

    fun requestBackClick(): Boolean {
        return if (hasUnsavedChanges()) {
            _uiState.update { it.copy(showCloseEditingDialog = true) }
            false
        } else {
            true
        }
    }

    fun dismissCloseEditingDialog() {
        _uiState.update { it.copy(showCloseEditingDialog = false) }
    }

    fun confirmCloseEditingDialog() {
        _uiState.update { it.copy(showCloseEditingDialog = false) }
    }

    fun hideHobby(item: HomeHobbySettingItemUiModel) {
        _uiState.update { state ->
            state.copy(
                activeHobbies = state.activeHobbies.filterNot { it.hobbyId == item.hobbyId },
                hiddenHobbies = state.hiddenHobbies + item.copy(isActive = false),
            )
        }
    }

    fun activateHobby(item: HomeHobbySettingItemUiModel) {
        _uiState.update { state ->
            if (state.activeHobbies.size >= MAX_ACTIVE_HOBBY_COUNT) {
                state.copy(showHobbyLimitDialog = true)
            } else {
                state.copy(
                    activeHobbies = state.activeHobbies + item.copy(isActive = true),
                    hiddenHobbies = state.hiddenHobbies.filterNot { it.hobbyId == item.hobbyId },
                )
            }
        }
    }

    fun moveHobby(from: Int, to: Int) {
        _uiState.update { state ->
            if (from == to || from !in state.activeHobbies.indices || to !in state.activeHobbies.indices) {
                state
            } else {
                state.copy(
                    activeHobbies = state.activeHobbies.toMutableList().apply {
                        add(to, removeAt(from))
                    }
                )
            }
        }
    }

    fun enterDeleteMode() {
        _uiState.update { it.copy(isDeleteMode = true, showAddDialog = false) }
    }

    fun requestEnterDeleteMode() {
        if (hasUnsavedChanges()) {
            pendingActionAfterSave = PendingActionAfterSave.ENTER_DELETE_MODE
            _uiState.update { it.copy(showSaveChangesDialog = true) }
        } else {
            enterDeleteMode()
        }
    }

    fun exitDeleteMode() {
        _uiState.update { it.copy(isDeleteMode = false, deleteTargetHobby = null) }
    }

    fun showDeleteHobbyDialog(item: HomeHobbySettingItemUiModel) {
        _uiState.update { it.copy(deleteTargetHobby = item) }
    }

    fun dismissDeleteHobbyDialog() {
        _uiState.update { it.copy(deleteTargetHobby = null) }
    }

    fun confirmDeleteHobby() = viewModelScope.launch {
        val targetHobby = _uiState.value.deleteTargetHobby ?: return@launch

        _uiState.update { it.copy(isSaving = true) }

        flow {
            emit(deleteHobbyUseCase(targetHobby.hobbyId))
        }.httpCatch(tag = "deleteHobby") { errorData ->
            _uiState.update {
                it.copy(
                    isSaving = false,
                    showSaveChangesDialog = false,
                    deleteTargetHobby = null,
                )
            }
            snackbarManager.show(errorData.message)
        }.collect { data ->
            _uiState.update { state ->
                val activeHobbies = state.activeHobbies.filterNot { it.hobbyId == data.data.hobbyId }
                val hiddenHobbies = state.hiddenHobbies.filterNot { it.hobbyId == data.data.hobbyId }
                updateOriginalHobbies(active = activeHobbies, hidden = hiddenHobbies)

                state.copy(
                    isSaving = false,
                    isDeleteMode = false,
                    deleteTargetHobby = null,
                    activeHobbies = activeHobbies,
                    hiddenHobbies = hiddenHobbies,
                )
            }
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun requestShowAddDialog() {
        if (hasUnsavedChanges()) {
            pendingActionAfterSave = PendingActionAfterSave.SHOW_ADD_DIALOG
            _uiState.update { it.copy(showSaveChangesDialog = true) }
        } else {
            showAddDialog()
        }
    }

    fun dismissAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun dismissSaveChangesDialog() {
        pendingActionAfterSave = null
        _uiState.update {
            it.copy(
                activeHobbies = originalActiveHobbies,
                hiddenHobbies = originalHiddenHobbies,
                showSaveChangesDialog = false,
            )
        }
    }

    fun confirmSaveChangesDialog() {
        saveHobbies()
    }

    fun confirmAddHobby(name: String) = viewModelScope.launch {
        val hobbyName = name.trim()
        if (hobbyName.isEmpty()) return@launch

        _uiState.update { it.copy(isSaving = true) }

        flow {
            emit(
                createHobbiesUseCase(
                    listOf(
                        CreateHobbyItemDomain(
                            hobbyInfoId = null,
                            hobbyName = hobbyName,
                        )
                    )
                )
            )
        }.httpCatch(tag = "createHobbies") { errorData ->
            _uiState.update { it.copy(isSaving = false) }
            snackbarManager.show(errorData.message)
        }.collect { data ->
            _uiState.update {
                it.copy(
                    isSaving = false,
                    showAddDialog = false,
                )
            }
            loadHobbies()
        }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun deleteAll() {
        _uiState.update { state ->
            state.copy(
                activeHobbies = emptyList(),
                hiddenHobbies = state.hiddenHobbies + state.activeHobbies.map { it.copy(isActive = false) },
                showDeleteDialog = false,
            )
        }
    }

    fun dismissHobbyLimitDialog() {
        _uiState.update { it.copy(showHobbyLimitDialog = false) }
    }

    fun saveHobbies() = viewModelScope.launch {
        val state = _uiState.value
        val progressHobbyList = state.activeHobbies.mapIndexed { index, item ->
            HomeHobbySettingProgressHobbyRequestDomain(
                hobbyId = item.hobbyId,
                sequence = index + 1,
            )
        }
        val hiddenHobbyList = state.hiddenHobbies.mapIndexed { index, item ->
            HomeHobbySettingHiddenHobbyRequestDomain(
                hobbyId = item.hobbyId,
                sequence = index + 1,
            )
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null, saveCompleted = false) }

        runCatching {
            updateHomeHobbySettingListUseCase(
                progressHobbyList = progressHobbyList,
                hiddenHobbyList = hiddenHobbyList,
            ).data
        }.onSuccess { data ->
            val active = data.progressHobbyList.map { it.toUiModel(isActive = true) }
            val hidden = data.hiddenHobbyList.map { it.toUiModel(isActive = false) }
            val pendingAction = pendingActionAfterSave
            pendingActionAfterSave = null
            updateOriginalHobbies(active = active, hidden = hidden)
            _uiState.update {
                it.copy(
                    isSaving = false,
                    activeHobbies = active,
                    hiddenHobbies = hidden,
                    showSaveChangesDialog = false,
                    isDeleteMode = pendingAction == PendingActionAfterSave.ENTER_DELETE_MODE,
                    showAddDialog = pendingAction == PendingActionAfterSave.SHOW_ADD_DIALOG,
                    saveCompleted = pendingAction == null,
                )
            }
        }.onFailure { throwable ->
            pendingActionAfterSave = null
            _uiState.update { it.copy(showSaveChangesDialog = false) }
            _uiState.update {
                it.copy(
                    isSaving = false,
                    errorMessage = throwable.message ?: "취미 설정을 저장하지 못했어요.",
                )
            }
        }
    }

    private fun HomeHobbySettingItemDomain.toUiModel(isActive: Boolean) = HomeHobbySettingItemUiModel(
        hobbyId = hobbyId,
        hobbyName = hobbyName,
        imageCode = imageIcon,
        isActive = isActive,
        deletable = deletable,
    )

    private fun hasUnsavedChanges(): Boolean {
        val state = _uiState.value
        val currentActiveHobbyIds = state.activeHobbies.map { it.hobbyId }
        val currentHiddenHobbyIds = state.hiddenHobbies.map { it.hobbyId }

        return currentActiveHobbyIds != originalActiveHobbyIds ||
            currentHiddenHobbyIds != originalHiddenHobbyIds
    }

    private fun updateOriginalHobbies(
        active: List<HomeHobbySettingItemUiModel>,
        hidden: List<HomeHobbySettingItemUiModel>,
    ) {
        originalActiveHobbies = active
        originalHiddenHobbies = hidden
        originalActiveHobbyIds = active.map { it.hobbyId }
        originalHiddenHobbyIds = hidden.map { it.hobbyId }
    }

    private companion object {
        private const val MAX_ACTIVE_HOBBY_COUNT = 10
    }

    private enum class PendingActionAfterSave {
        ENTER_DELETE_MODE,
        SHOW_ADD_DIALOG,
    }
}
