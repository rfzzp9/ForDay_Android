package com.forday.app.presentation.home.hobbysetting

data class HomeHobbySettingItemUiModel(
    val hobbyId: Long,
    val hobbyName: String,
    val imageCode: String,
    val isActive: Boolean,
    val deletable: Boolean,
)

data class HomeHobbySettingUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleteMode: Boolean = false,
    val activeHobbies: List<HomeHobbySettingItemUiModel> = emptyList(),
    val hiddenHobbies: List<HomeHobbySettingItemUiModel> = emptyList(),
    val showAddDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val deleteTargetHobby: HomeHobbySettingItemUiModel? = null,
    val showCloseEditingDialog: Boolean = false,
    val showSaveChangesDialog: Boolean = false,
    val showHobbyLimitDialog: Boolean = false,
    val errorMessage: String? = null,
)
