package com.forday.app.core.designsystem.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SaveChangesDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    CommonDialog(
        title = "변경된 내용을 저장하시겠습니까?",
        bodyText = "취소하면 변경된 내용이 초기화됩니다.",
        showCloseIcon = false,
        primaryButtonText = "저장",
        secondaryButtonText = "취소",
        isSecondaryButtonVisible = true,
        onPrimaryClick = onConfirm,
        onSecondaryClick = onDismiss,
        onDismiss = onDismiss,
    )
}

@Preview(showBackground = true, backgroundColor = 0x99000000, widthDp = 360)
@Composable
private fun SaveChangesDialogPreview() {
    SaveChangesDialog(onDismiss = {}, onConfirm = {})
}
