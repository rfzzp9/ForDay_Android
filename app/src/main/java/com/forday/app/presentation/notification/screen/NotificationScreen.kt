package com.forday.app.presentation.notification.screen

import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dayn.forday.R
import com.forday.app.core.designsystem.theme.FordayColor
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.domain.model.NotificationItemDomain
import com.forday.app.domain.model.NotificationTypeDomain
import com.forday.app.core.designsystem.dialog.NotificationPermissionDialog
import com.forday.app.presentation.notification.NotificationSideEffect
import com.forday.app.presentation.notification.NotificationUiState
import com.forday.app.presentation.notification.NotificationViewModel

@Composable
fun NotificationRoute(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigateToRecord: (recordId: Long, notificationId: Long) -> Unit,
    onPermissionGranted: () -> Unit = {},
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showNotificationPermissionDialog by remember { mutableStateOf(false) }
    var waitingForPermission by remember { mutableStateOf(false) }
    var navigateToAppSettings by remember { mutableStateOf(false) }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadNotifications()
            if (waitingForPermission && NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                onPermissionGranted()
                waitingForPermission = false
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is NotificationSideEffect.OpenNotificationSettings -> {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                }
                is NotificationSideEffect.Exception -> {
                    effect.throwable.printStackTrace()
                }
            }
        }
    }

    NotificationScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onSettingsClick = onSettingsClick,
        onPermissionBannerClick = {
            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                navigateToAppSettings = false
                showNotificationPermissionDialog = true
            } else {
                navigateToAppSettings = true
                showNotificationPermissionDialog = true
            }
        },
        onLoadMore = { viewModel.loadMore() },
        onItemClick = { item ->
            val recordId = when (item.type) {
                NotificationTypeDomain.RECORD_REACTION -> item.reactionAlarm?.recordId
                NotificationTypeDomain.RECORD_COMMENT -> item.commentAlarm?.recordId
                NotificationTypeDomain.UNKNOWN -> null
            }
            recordId?.let { onNavigateToRecord(it.toLong(), item.notificationId.toLong()) }
        }
    )

    if (showNotificationPermissionDialog) {
        NotificationPermissionDialog(
            onDismiss = { showNotificationPermissionDialog = false },
            onConfirm = {
                showNotificationPermissionDialog = false
                if (navigateToAppSettings) {
                    onSettingsClick()
                } else {
                    waitingForPermission = true
                    viewModel.onPermissionBannerClick()
                }
            }
        )
    }
}

@Composable
private fun NotificationScreen(
    uiState: NotificationUiState,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPermissionBannerClick: () -> Unit,
    onLoadMore: () -> Unit,
    onItemClick: (NotificationItemDomain) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            NotificationTopBar(
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.notifications.isEmpty() && !uiState.isLoading) {
                NotificationEmptyContent(
                    showPermissionBanner = uiState.showPermissionBanner,
                    onPermissionBannerClick = onPermissionBannerClick
                )
            } else {
                NotificationListContent(
                    notifications = uiState.notifications,
                    showPermissionBanner = uiState.showPermissionBanner,
                    hasNext = uiState.hasNext,
                    onPermissionBannerClick = onPermissionBannerClick,
                    onLoadMore = onLoadMore,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationTopBar(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "내 알림",
                style = ForDayTheme.typography.title16,
                color = FordayColor.Gray800
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_chevron_left),
                    contentDescription = "뒤로가기",
                    tint = FordayColor.Gray800
                )
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_settings),
                    contentDescription = "설정",
                    tint = FordayColor.Gray800
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
private fun NotificationEmptyContent(
    showPermissionBanner: Boolean,
    onPermissionBannerClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (showPermissionBanner) {
            NotificationPermissionBanner(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                onClick = onPermissionBannerClick
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Box(
                modifier = Modifier.height(162.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.box_img),
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 160.dp, height = 140.dp)
                        .align(Alignment.BottomCenter)
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.TopCenter),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_sad),
                        contentDescription = null,
                        modifier = Modifier.size(25.7.dp)
                    )
                }
            }

            Text(
                text = "새로운 알림이 없어요.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = (14 * 1.4).sp,
                color = Color(0xFF7A7A7A),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NotificationPermissionBanner(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FordayColor.Neutral50)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "기기 알림 설정이 꺼져있어요.",
                style = ForDayTheme.typography.body14,
                color = FordayColor.Gray800
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = FordayColor.Gray500)) {
                        append("알림을 놓치지 않도록 ")
                    }
                    withStyle(SpanStyle(color = Color(0xFFFF9447))) {
                        append("알림 권한")
                    }
                    withStyle(SpanStyle(color = FordayColor.Gray500)) {
                        append("을 허용해주세요.")
                    }
                },
                style = ForDayTheme.typography.label12
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.icon_chevron_right),
            contentDescription = null,
            tint = Color(0xFFB5B5B5),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun NotificationListContent(
    notifications: List<NotificationItemDomain>,
    showPermissionBanner: Boolean,
    hasNext: Boolean,
    onPermissionBannerClick: () -> Unit,
    onLoadMore: () -> Unit,
    onItemClick: (NotificationItemDomain) -> Unit
) {
    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            hasNext && lastVisibleIndex >= listState.layoutInfo.totalItemsCount - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp)
    ) {
        if (showPermissionBanner) {
            item {
                NotificationPermissionBanner(
                    modifier = Modifier.padding(bottom = 8.dp),
                    onClick = onPermissionBannerClick
                )
            }
        }

        items(
            items = notifications,
            key = { it.notificationId }
        ) { item ->
            NotificationListItem(
                item = item,
                onItemClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
private fun NotificationListItem(
    item: NotificationItemDomain,
    onItemClick: () -> Unit
) {
    val backgroundColor = if (item.isUnread) Color(0xFFFFF5EE) else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onItemClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 프로필 이미지 (36dp)
            Box(modifier = Modifier.size(36.dp)) {
                if (item.senderProfileUrl != null) {
                    AsyncImage(
                        model = item.senderProfileUrl,
                        contentDescription = "프로필",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(FordayColor.Gray03)
                    )
                }

                // 반응 배지 (우측 하단)
                val reactionDrawable = when (item.reactionAlarm?.reactionType) {
                    "AWESOME" -> R.drawable.alarm_awesome
                    "GREAT" -> R.drawable.alarm_great
                    "FIGHTING" -> R.drawable.alarm_fighting
                    "AMAZING" -> R.drawable.alarm_amazing
                    else -> null
                }
                if (item.type == NotificationTypeDomain.RECORD_REACTION && reactionDrawable != null) {
                    Image(
                        painter = painterResource(id = reactionDrawable),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            // 텍스트 영역
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.createdAt,
                    style = ForDayTheme.typography.label12,
                    color = Color(0xFFB5B5B5)
                )
                Text(
                    text = item.message,
                    style = ForDayTheme.typography.label14,
                    color = FordayColor.Gray800,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 썸네일 이미지 (48dp, 있을 때만)
            if (item.imageUrl != null && item.imageUrl != "") {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = "첨부 이미지",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = FordayColor.Gray03,
                            shape = RoundedCornerShape(8.dp)
                        )
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp),
            thickness = 1.dp,
            color = FordayColor.MediumGray
        )
    }
}
