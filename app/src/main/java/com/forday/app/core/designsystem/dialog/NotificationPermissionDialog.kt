package com.forday.app.core.designsystem.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val Neutral900 = Color(0xFF1E1E1E)
private val Neutral800 = Color(0xFF3A3A3A)
private val Action001 = Color(0xFFFF9447)
private val Action003 = Color(0xFFE5E5E5)

@Composable
fun NotificationPermissionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        NotificationPermissionDialogContent(
            onDismiss = onDismiss,
            onConfirm = onConfirm
        )
    }
}

@Composable
fun NotificationPermissionDialogContent(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 18.dp)
        ) {
            Text(
                text = "알림 권한 허용",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 10.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = (18 * 1.2).sp,
                color = Neutral900
            )

            Text(
                text = "알림이 꺼져있으면 중요한 소식을 놓칠 수 있어요.\n기기설정에서 알림을 허용해 주세요.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = (14 * 1.4).sp,
                color = Neutral800,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Action003,
                        contentColor = Neutral900
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "취소",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = (14 * 1.2).sp
                    )
                }

                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Action001,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "설정하기",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = (14 * 1.2).sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun NotificationPermissionDialogPreview() {
    NotificationPermissionDialogContent(
        onDismiss = {},
        onConfirm = {}
    )
}
