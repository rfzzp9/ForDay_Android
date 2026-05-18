package com.forday.app.presentation.home.hobbysetting

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dayn.forday.R
import com.forday.app.core.designsystem.dialog.AddHobbyDialog
import com.forday.app.core.designsystem.dialog.CloseEditingDialog
import com.forday.app.core.designsystem.dialog.CommonDialog
import com.forday.app.core.designsystem.dialog.DeleteHobbyDialog
import com.forday.app.core.designsystem.dialog.SaveChangesDialog
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.core.util.ImageCodeMapper
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

private val ColorPrimary = Color(0xFFFF9447)
private val ColorNeutral900 = Color(0xFF1E1E1E)
private val ColorNeutral800 = Color(0xFF3A3A3A)
private val ColorNeutral400 = Color(0xFFB5B5B5)
private val ColorWhite = Color.White
private const val ACTIVE_HOBBY_LIST_START_INDEX = 2
private const val MAX_ACTIVE_HOBBY_COUNT = 10

@Composable
fun HomeHobbySettingRoute(
    onBackClick: () -> Unit,
    viewModel: HomeHobbySettingViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadHobbies()
    }

    LaunchedEffect(state.saveCompleted) {
        if (state.saveCompleted) onBackClick()
    }

    val handleBackClick = {
        if (viewModel.requestBackClick()) {
            onBackClick()
        }
    }

    BackHandler {
        handleBackClick()
    }

    HomeHobbySettingScreen(
        state = state,
        onBackClick = handleBackClick,
        onAddClick = viewModel::requestShowAddDialog,
        onDeleteClick = viewModel::requestEnterDeleteMode,
        onExitDeleteMode = viewModel::exitDeleteMode,
        onHideHobby = viewModel::hideHobby,
        onActivateHobby = viewModel::activateHobby,
        onDeleteHobbyClick = viewModel::showDeleteHobbyDialog,
        onMoveHobby = viewModel::moveHobby,
        onSaveClick = viewModel::saveHobbies,
        onDismissDeleteDialog = viewModel::dismissDeleteDialog,
        onDismissAddDialog = viewModel::dismissAddDialog,
        onConfirmAddHobby = viewModel::confirmAddHobby,
        onDismissDeleteHobbyDialog = viewModel::dismissDeleteHobbyDialog,
        onConfirmDeleteHobby = viewModel::confirmDeleteHobby,
        onConfirmDeleteAll = viewModel::deleteAll,
        onDismissCloseEditingDialog = viewModel::dismissCloseEditingDialog,
        onConfirmCloseEditingDialog = {
            viewModel.confirmCloseEditingDialog()
            onBackClick()
        },
        onDismissSaveChangesDialog = viewModel::dismissSaveChangesDialog,
        onConfirmSaveChangesDialog = viewModel::confirmSaveChangesDialog,
        onDismissHobbyLimitDialog = viewModel::dismissHobbyLimitDialog,
    )
}

@Composable
fun HomeHobbySettingScreen(
    state: HomeHobbySettingUiState,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onExitDeleteMode: () -> Unit,
    onHideHobby: (HomeHobbySettingItemUiModel) -> Unit,
    onActivateHobby: (HomeHobbySettingItemUiModel) -> Unit,
    onDeleteHobbyClick: (HomeHobbySettingItemUiModel) -> Unit,
    onMoveHobby: (from: Int, to: Int) -> Unit,
    onSaveClick: () -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onDismissAddDialog: () -> Unit,
    onConfirmAddHobby: (String) -> Unit,
    onDismissDeleteHobbyDialog: () -> Unit,
    onConfirmDeleteHobby: () -> Unit,
    onConfirmDeleteAll: () -> Unit,
    onDismissCloseEditingDialog: () -> Unit,
    onConfirmCloseEditingDialog: () -> Unit,
    onDismissSaveChangesDialog: () -> Unit,
    onConfirmSaveChangesDialog: () -> Unit,
    onDismissHobbyLimitDialog: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorWhite)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HobbyTopAppBar(
                isDeleteMode = state.isDeleteMode,
                isActiveHobbyLimitReached = state.activeHobbies.size >= MAX_ACTIVE_HOBBY_COUNT,
                onBackClick = onBackClick,
                onDeleteClick = onDeleteClick,
                onAddClick = onAddClick,
                onCloseDeleteModeClick = onExitDeleteMode,
            )

            when {
                state.isLoading -> LoadingContent(modifier = Modifier.weight(1f))
                state.errorMessage != null -> ErrorContent(
                    message = state.errorMessage,
                    modifier = Modifier.weight(1f),
                )
                else -> HobbySettingList(
                    state = state,
                    onHideHobby = onHideHobby,
                    onActivateHobby = onActivateHobby,
                    onDeleteHobbyClick = onDeleteHobbyClick,
                    onMoveHobby = onMoveHobby,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        BottomSaveButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            enabled = !state.isLoading && !state.isSaving,
            isSaving = state.isSaving,
            onSaveClick = onSaveClick,
        )

        if (state.showDeleteDialog) {
            DeleteConfirmDialog(
                onDismiss = onDismissDeleteDialog,
                onConfirm = onConfirmDeleteAll,
            )
        }

        if (state.showAddDialog) {
            AddHobbyDialog(
                onDismiss = onDismissAddDialog,
                onConfirm = onConfirmAddHobby,
            )
        }

        if (state.deleteTargetHobby != null) {
            DeleteHobbyDialog(
                onDismiss = onDismissDeleteHobbyDialog,
                onConfirm = onConfirmDeleteHobby,
            )
        }

        if (state.showCloseEditingDialog) {
            CloseEditingDialog(
                onDismiss = onDismissCloseEditingDialog,
                onConfirm = onConfirmCloseEditingDialog,
            )
        }

        if (state.showSaveChangesDialog) {
            SaveChangesDialog(
                onDismiss = onDismissSaveChangesDialog,
                onConfirm = onConfirmSaveChangesDialog,
            )
        }

        if (state.showHobbyLimitDialog) {
            HobbyLimitDialog(onDismiss = onDismissHobbyLimitDialog)
        }
    }
}

@Composable
private fun HobbyTopAppBar(
    isDeleteMode: Boolean,
    isActiveHobbyLimitReached: Boolean,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddClick: () -> Unit,
    onCloseDeleteModeClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = "뒤로가기",
            tint = ColorNeutral800,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(24.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBackClick,
                ),
        )

        Text(
            text = "취미 설정",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = (16 * 1.2).sp,
                color = ColorNeutral800,
            ),
        )

        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!isDeleteMode) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trash),
                    contentDescription = "전체 삭제",
                    tint = ColorNeutral800,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDeleteClick,
                        ),
                )
            }
            if (isDeleteMode || !isActiveHobbyLimitReached) {
                Icon(
                painter = painterResource(id = if (isDeleteMode) R.drawable.ic_close else R.drawable.ic_plus),
                contentDescription = if (isDeleteMode) "삭제 모드 닫기" else "취미 추가",
                tint = ColorNeutral800,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = if (isDeleteMode) onCloseDeleteModeClick else onAddClick,
                    ),
                )
            }
        }
    }
}

@Composable
private fun HobbySettingList(
    state: HomeHobbySettingUiState,
    onHideHobby: (HomeHobbySettingItemUiModel) -> Unit,
    onActivateHobby: (HomeHobbySettingItemUiModel) -> Unit,
    onDeleteHobbyClick: (HomeHobbySettingItemUiModel) -> Unit,
    onMoveHobby: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromIndex = from.index - ACTIVE_HOBBY_LIST_START_INDEX
        val toIndex = to.index - ACTIVE_HOBBY_LIST_START_INDEX

        if (
            fromIndex in state.activeHobbies.indices &&
            toIndex in state.activeHobbies.indices &&
            fromIndex != toIndex
        ) {
            onMoveHobby(fromIndex, toIndex)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 96.dp),
    ) {
        item {
            Text(
                text = "+ - 버튼을 눌러 최대 10개 까지 추가할 수 있어요.\n꾹 눌러서 이동하면 노출 순서를 변경할 수 있어요.",
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = (14 * 1.4).sp,
                    color = ColorNeutral800,
                ),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 8.dp),
            )
        }

        item {
            SectionTitle(
                text = "진행중인 취미",
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        if (state.activeHobbies.isEmpty()) {
            item { EmptyRow(text = "진행중인 취미가 없어요.") }
        } else {
            itemsIndexed(
                items = state.activeHobbies,
                key = { _, item -> item.hobbyId },
            ) { index, item ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = item.hobbyId,
                ) { _ ->
                    ActiveHobbyRow(
                        item = item,
                        onMinus = { onHideHobby(item) },
                        onDeleteClick = { onDeleteHobbyClick(item) },
                        dragHandleModifier = if (state.isDeleteMode) Modifier else Modifier.draggableHandle(),
                        isDeleteMode = state.isDeleteMode,
                    )
                }
            }
        }

        item {
            Image(
                painter = painterResource(id = R.drawable.dot_line),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentScale = ContentScale.FillWidth,
            )
        }

        item {
            SectionTitle(text = "숨겨진 취미")
        }

        if (state.hiddenHobbies.isEmpty()) {
            item { EmptyRow(text = "숨겨진 취미가 없어요.") }
        } else {
            itemsIndexed(
                items = state.hiddenHobbies,
                key = { _, item -> "hidden_${item.hobbyId}" },
            ) { index, item ->
                HiddenHobbyRow(
                    item = item,
                    onPlus = { onActivateHobby(item) },
                    onDeleteClick = { onDeleteHobbyClick(item) },
                    isDeleteMode = state.isDeleteMode,
                    isActiveHobbyLimitReached = state.activeHobbies.size >= MAX_ACTIVE_HOBBY_COUNT,
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = (14 * 1.4).sp,
            color = ColorNeutral400,
        ),
        modifier = modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 4.dp),
    )
}

@Composable
private fun ActiveHobbyRow(
    item: HomeHobbySettingItemUiModel,
    onMinus: () -> Unit,
    onDeleteClick: () -> Unit,
    dragHandleModifier: Modifier,
    isDeleteMode: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(ColorWhite)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.home_hobby_menu),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(24.dp)
                    .then(dragHandleModifier),
            )

            HobbyIcon(item = item)

            Text(
                text = item.hobbyName,
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = ColorNeutral800,
                ),
            )
        }

        if (isDeleteMode) {
            CircleIconButton(
                iconRes = R.drawable.ic_trash,
                contentDescription = "취미 삭제",
                onClick = onDeleteClick,
            )
        } else {
            CircleIconButton(
                iconRes = R.drawable.ic_hide,
                contentDescription = "취미 숨기기",
                onClick = onMinus,
            )
        }
    }
}

@Composable
private fun HiddenHobbyRow(
    item: HomeHobbySettingItemUiModel,
    onPlus: () -> Unit,
    onDeleteClick: () -> Unit,
    isDeleteMode: Boolean,
    isActiveHobbyLimitReached: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            HobbyIcon(item = item)
            Text(
                text = item.hobbyName,
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = ColorNeutral800,
                ),
            )
        }

        if (isDeleteMode) {
            CircleIconButton(
                iconRes = R.drawable.ic_trash,
                contentDescription = "취미 삭제",
                onClick = onDeleteClick,
            )
        } else if (!isActiveHobbyLimitReached) {
            CircleIconButton(
                iconRes = R.drawable.ic_active,
                contentDescription = "취미 활성화",
                onClick = onPlus,
            )
        }
    }
}

@Composable
private fun CircleIconButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = Color.Unspecified,
        )
    }
}

@Composable
private fun HobbyIcon(
    item: HomeHobbySettingItemUiModel,
) {
    Icon(
        painter = painterResource(id = ImageCodeMapper.getDrawableResId(item.imageCode)),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier.size(24.dp),
    )
}

@Composable
private fun EmptyRow(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
//            .height(56.dp)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Image(
                painter            = painterResource(id = R.drawable.icon_sad),
                contentDescription = null,
                modifier           = Modifier.size(48.dp),
            )
            Text(
                text = "숨겨진 취미가 없어요",
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize   = 14.sp,
                    lineHeight = (14 * 1.4).sp,
                    color      = ColorNeutral400,
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun BottomSaveButton(
    enabled: Boolean,
    isSaving: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(
                brush = Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.38f to ColorWhite,
                    1f to ColorWhite,
                )
            ),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (enabled) ColorPrimary else ColorNeutral400)
                .clickable(enabled = enabled, onClick = onSaveClick),
            contentAlignment = Alignment.Center,
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = ColorWhite,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = "저장",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = (16 * 1.2).sp,
                        color = ColorWhite,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = ColorPrimary)
    }
}

@Composable
private fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = TextStyle(
                fontSize = 14.sp,
                color = ColorNeutral800,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Composable
private fun DeleteConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    CommonDialog(
        title = "취미를 모두 삭제하시겠습니까?",
        bodyText = "삭제된 취미는 숨겨진 취미로 이동됩니다.",
        showCloseIcon = false,
        primaryButtonText = "확인",
        secondaryButtonText = "취소",
        isSecondaryButtonVisible = true,
        onPrimaryClick = onConfirm,
        onSecondaryClick = onDismiss,
        onDismiss = onDismiss,
    )
}

@Composable
private fun HobbyLimitDialog(onDismiss: () -> Unit) {
    CommonDialog(
        title = "최대 10개까지만 추가할 수 있어요",
        bodyText = "취미는 + - 버튼을 눌러 최대 10개까지 추가할 수 있어요.",
        showCloseIcon = false,
        primaryButtonText = "확인",
        isSecondaryButtonVisible = false,
        onPrimaryClick = onDismiss,
        onDismiss = onDismiss,
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun HomeHobbySettingScreenPreview() {
    val sampleState = HomeHobbySettingUiState(
        activeHobbies = listOf(
            HomeHobbySettingItemUiModel(0, "독서", "READING_ICON", true, true),
            HomeHobbySettingItemUiModel(1, "러닝", "RUNNING_ICON", true, true),
            HomeHobbySettingItemUiModel(2, "요리", "COOKING_ICON", true, true),
        ),
        hiddenHobbies = listOf(
            HomeHobbySettingItemUiModel(3, "그림 그리기", "DRAWING_ICON", false, true),
            HomeHobbySettingItemUiModel(4, "헬스", "GYM_ICON", false, true),
        ),
    )

    ForDayTheme {
        HomeHobbySettingScreen(
            state = sampleState,
            onBackClick = {},
            onAddClick = {},
            onDeleteClick = {},
            onExitDeleteMode = {},
            onHideHobby = {},
            onActivateHobby = {},
            onDeleteHobbyClick = {},
            onMoveHobby = { _, _ -> },
            onSaveClick = {},
            onDismissDeleteDialog = {},
            onDismissAddDialog = {},
            onConfirmAddHobby = {},
            onDismissDeleteHobbyDialog = {},
            onConfirmDeleteHobby = {},
            onConfirmDeleteAll = {},
            onDismissCloseEditingDialog = {},
            onConfirmCloseEditingDialog = {},
            onDismissSaveChangesDialog = {},
            onConfirmSaveChangesDialog = {},
            onDismissHobbyLimitDialog = {},
        )
    }
}
