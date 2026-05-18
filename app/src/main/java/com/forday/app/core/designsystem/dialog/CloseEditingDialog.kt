package com.forday.app.core.designsystem.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CloseEditingDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    CommonDialog(
        title = "편집 중인 화면을 닫으시겠습니까?",
        bodyText = "지금까지 변경한 내용은 저장되지 않습니다.",
        showCloseIcon = false,
        primaryButtonText = "닫기",
        secondaryButtonText = "취소",
        isSecondaryButtonVisible = true,
        onPrimaryClick = onConfirm,
        onSecondaryClick = onDismiss,
        onDismiss = onDismiss,
    )
}

@Preview(showBackground = true, backgroundColor = 0x99000000, widthDp = 360)
@Composable
private fun CloseEditingDialogPreview() {
    CloseEditingDialog(onDismiss = {}, onConfirm = {})
}
