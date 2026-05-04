package com.forday.app.presentation.allsettings.settings.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.foundation.layout.Arrangement
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.dayn.forday.R
import com.forday.app.core.designsystem.dialog.NotificationPermissionDialog
import com.forday.app.core.designsystem.theme.ForDayTheme

@Composable
private fun ForDayToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackColor = if (checked) Color(0xFFFF9447) else Color(0xFFE5E5E5)
    val thumbOffset = if (checked) 14.dp else 0.dp

    Box(
        modifier = modifier
            .width(36.dp)
            .height(22.dp)
            .clip(RoundedCornerShape(11.5.dp))
            .background(trackColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(start = thumbOffset + 3.dp)
                .size(16.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
private fun ToggleSettingRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = ForDayTheme.typography.body14,
                color = Color(0xFF1E1E1E)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = ForDayTheme.typography.label10,
                    color = Color(0xFF7A7A7A)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        ForDayToggle(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onTermsOfServiceClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onCancelAccountClick: () -> Unit = {},
    postLikeNotificationEnabled: Boolean = false,
    pushNotificationEnabled: Boolean = false,
    onPostLikeNotificationToggle: (Boolean) -> Unit = {},
    onPushNotificationToggle: (Boolean) -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showNotificationPermissionDialog by remember { mutableStateOf(false) }
    var pendingPostLikeEnable by remember { mutableStateOf(false) }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            if (pendingPostLikeEnable && NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                onPostLikeNotificationToggle(true)
                pendingPostLikeEnable = false
            }
        }
    }

    fun handleNotificationToggle(newValue: Boolean, onToggle: (Boolean) -> Unit, onPending: (() -> Unit)? = null) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            onPending?.invoke()
            showNotificationPermissionDialog = true
        } else {
            onToggle(newValue)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            SettingsHeader(onBackClick = onBackClick)

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                // 알림 섹션
                SectionTitle(text = "알림")

                Spacer(modifier = Modifier.height(16.dp))

                ToggleSettingRow(
                    title = "게시글 좋아요 알림 설정",
                    subtitle = "내 게시글에 감정 기록이 달리면 알려드려요.",
                    checked = postLikeNotificationEnabled,
                    onCheckedChange = { handleNotificationToggle(it, onPostLikeNotificationToggle, onPending = { pendingPostLikeEnable = true }) }
                )

                ToggleSettingRow(
                    title = "앱 푸시 알림 설정",
                    checked = pushNotificationEnabled,
                    onCheckedChange = { handleNotificationToggle(it, onPushNotificationToggle) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 앱내 섹션
                SectionTitle(text = "안내")

                Spacer(modifier = Modifier.height(16.dp))

                // 앱 버전
                val versionName = remember {
                    context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
                }
                AppVersionItem(version = versionName)

                Spacer(modifier = Modifier.height(16.dp))

                // 서비스 이용약관
                SettingsMenuItem(
                    text = "서비스 이용약관",
                    onClick = onTermsOfServiceClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 개인정보 보호정책
                SettingsMenuItem(
                    text = "개인정보 보호정책",
                    onClick = onPrivacyPolicyClick
                )

                Spacer(modifier = Modifier.height(32.dp))

                Spacer(modifier = Modifier.height(16.dp))

                // 로그아웃
                SettingsMenuItem(
                    text = "로그아웃",
                    onClick = { showLogoutDialog = true }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 탈퇴하기 섹션
                SectionTitle(
                    text = "탈퇴하기",
                    onClick = onCancelAccountClick
                )
            }
        }
    }

    // 로그아웃 다이얼로그
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            }
        )
    }

    // 알림 권한 다이얼로그
    if (showNotificationPermissionDialog) {
        NotificationPermissionDialog(
            onDismiss = { showNotificationPermissionDialog = false },
            onConfirm = {
                showNotificationPermissionDialog = false
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            }
        )
    }
}

@Composable
private fun SettingsHeader(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        IconButton(
            onClick = rememberThrottledClick { onBackClick() },
            interactionSource = remember { NoRippleInteractionSource() },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_chevron_left),
                contentDescription = "뒤로가기",
                tint = Color.Black
            )
        }

        Text(
            text = "전체설정",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun SectionTitle(
    text: String,
    onClick: (() -> Unit)? = null
) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFB0B0B0)
        ),
        modifier = Modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        onClick = rememberThrottledClick { onClick() },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                } else {
                    Modifier
                }
            )
    )
}

@Composable
private fun AppVersionItem(version: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "앱 버전",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        )

        Text(
            text = version,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFB0B0B0)
            )
        )
    }
}

@Composable
private fun SettingsMenuItem(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(
                onClick = rememberThrottledClick { onClick() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        )

        Icon(
            painter = painterResource(id = R.drawable.icon_chevron_right),
            contentDescription = "이동",
            tint = Color(0xFFB0B0B0)
        )
    }
}

@Composable
private fun LogoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(top = 24.dp, bottom = 24.dp)
        ) {
            // 제목
            Text(
                text = "로그아웃 하시겠습니까?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E1E),
                lineHeight = 21.6.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 10.dp)
            )

            // 버튼들
            var closeTextSize by remember { mutableStateOf(14.sp) }
            var logoutTextSize by remember { mutableStateOf(14.sp) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 닫기 버튼
                DialogButton(
                    text = "닫기",
                    backgroundColor = Color(0xFFE5E5E5),
                    textColor = Color(0xFF1E1E1E),
                    textSize = closeTextSize,
                    onTextSizeChange = { closeTextSize = it },
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss
                )

                // 로그아웃 버튼
                DialogButton(
                    text = "로그아웃",
                    backgroundColor = Color(0xFFFF9447),
                    textColor = Color.White,
                    textSize = logoutTextSize,
                    onTextSizeChange = { logoutTextSize = it },
                    modifier = Modifier.weight(1f),
                    onClick = rememberThrottledClick { onConfirm() }
                )
            }
        }
    }
}

@Composable
private fun DialogButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    textSize: TextUnit,
    onTextSizeChange: (TextUnit) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(backgroundColor)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = text,
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center,
            lineHeight = 16.8.sp,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            softWrap = false,
            onTextLayout = { result ->
                if (result.hasVisualOverflow) onTextSizeChange(textSize * 0.9f)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    ForDayTheme {
        SettingsScreen(
            onBackClick = {},
            onTermsOfServiceClick = {},
            onPrivacyPolicyClick = {},
        )
    }
}