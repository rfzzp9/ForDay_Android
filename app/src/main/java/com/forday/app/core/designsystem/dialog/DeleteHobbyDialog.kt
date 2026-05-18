package com.forday.app.core.designsystem.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DeleteHobbyDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    CommonDialog(
        title = "이 취미를 삭제하시겠어요?",
        bodyText = "삭제 시 관련된 모든 활동 기록글이 삭제되며\n복구할 수 없습니다.",
        showCloseIcon = false,
        primaryButtonText = "삭제하기",
        secondaryButtonText = "닫기",
        isSecondaryButtonVisible = true,
        onPrimaryClick = onConfirm,
        onSecondaryClick = onDismiss,
        onDismiss = onDismiss,
    )
}

@Preview(showBackground = true, backgroundColor = 0x99000000, widthDp = 360)
@Composable
private fun DeleteHobbyDialogPreview() {
    DeleteHobbyDialog(onDismiss = {}, onConfirm = {})
}
