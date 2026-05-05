package com.forday.app.presentation.main.component

import androidx.compose.runtime.Composable
import com.forday.app.core.designsystem.dialog.RoutineOnlyOneHaveDialog

@Composable
internal fun AlreadyRecordedDialogHost(
    visible: Boolean,
    todayRecordId: Int?,
    onDismiss: () -> Unit,
    onViewRecords: (Int) -> Unit,
) {
    if (visible) {
        RoutineOnlyOneHaveDialog(
            onDismiss = onDismiss,
            onViewRecords = {
                onDismiss()
                todayRecordId?.let(onViewRecords)
            }
        )
    }
}
