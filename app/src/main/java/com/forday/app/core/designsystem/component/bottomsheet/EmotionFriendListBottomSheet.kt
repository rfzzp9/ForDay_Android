package com.forday.app.core.designsystem.component.bottomsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dayn.forday.R
import com.forday.app.core.designsystem.theme.ForDayTheme

/**
 * 감정 유형
 */
enum class EmotionType(val emoji: String, val apiKey: String) {
    SUN("☀️", "AWESOME"),
    LIKE("👍", "GREAT"),
    PARTY("🎉", "AMAZING"),
    FIRE("🔥", "FIGHTING");

    companion object {
        fun fromApiKey(key: String): EmotionType? = entries.find { it.apiKey == key }
    }
}

/**
 * 감정 탭 (전체 포함)
 */
sealed class EmotionTab {
    data object All : EmotionTab()
    data class Emotion(val type: EmotionType) : EmotionTab()

    fun toApiKey(): String? = when (this) {
        is All -> null
        is Emotion -> type.apiKey
    }
}

/**
 * 감정을 남긴 친구 아이템
 */
data class EmotionFriend(
    val id: String,
    val nickname: String,
    val profileImageUrl: String?,
    val emotionType: EmotionType,
)

/**
 * 탭별 카운트 정보
 */
data class EmotionSummary(
    val totalCount: Int = 0,
    val awesome: Int = 0,
    val great: Int = 0,
    val amazing: Int = 0,
    val fighting: Int = 0,
)

// ────────────────────────────────────────────────────────────
// Design Tokens
// ────────────────────────────────────────────────────────────

private val ColorPrimary002 = Color(0xFFFFE6D1)
private val ColorBadgeBorder = Color(0xFFFF9447)
private val ColorNeutral900 = Color(0xFF1E1E1E)
private val ColorNeutral800 = Color(0xFF3A3A3A)
private val ColorNeutral400 = Color(0xFFB5B5B5)
private val ColorBgWhite = Color.White

// ────────────────────────────────────────────────────────────
// Main Composable
// ────────────────────────────────────────────────────────────

/**
 * 감정 남긴 친구 목록 바텀시트
 *
 * @param summary          탭별 카운트 (v2 API reactionSummary)
 * @param friendsByTab     탭 key("ALL","AWESOME",...) → 친구 목록 map
 * @param hasNextByTab     탭 key → hasNext 여부 map
 * @param lastReactionIdByTab 탭 key → lastReactionId map
 * @param selectedTab      현재 선택된 탭
 * @param onTabSelected    탭 변경 콜백
 * @param onLoadMore       추가 로드 콜백 (탭 key, lastReactionId)
 * @param onDismiss        시트 닫기 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionFriendListBottomSheet(
    summary: EmotionSummary,
    friendsByTab: Map<String, List<EmotionFriend>>,
    hasNextByTab: Map<String, Boolean>,
    lastReactionIdByTab: Map<String, Long?>,
    selectedTab: EmotionTab,
    onTabSelected: (EmotionTab) -> Unit,
    onLoadMore: (tabKey: String, lastReactionId: Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    val countByEmotion = remember(summary) {
        mapOf(
            EmotionType.SUN to summary.awesome,
            EmotionType.LIKE to summary.great,
            EmotionType.PARTY to summary.amazing,
            EmotionType.FIRE to summary.fighting,
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = ColorBgWhite,
        dragHandle = null,
    ) {
        SheetContent(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            friendsByTab = friendsByTab,
            hasNextByTab = hasNextByTab,
            lastReactionIdByTab = lastReactionIdByTab,
            totalCount = summary.totalCount,
            countByEmotion = countByEmotion,
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onLoadMore = onLoadMore,
        )
    }
}

// ────────────────────────────────────────────────────────────
// Sheet Content
// ────────────────────────────────────────────────────────────

@Composable
private fun SheetContent(
    modifier: Modifier = Modifier,
    friendsByTab: Map<String, List<EmotionFriend>>,
    hasNextByTab: Map<String, Boolean>,
    lastReactionIdByTab: Map<String, Long?>,
    totalCount: Int,
    countByEmotion: Map<EmotionType, Int>,
    selectedTab: EmotionTab,
    onTabSelected: (EmotionTab) -> Unit,
    onLoadMore: (tabKey: String, lastReactionId: Long) -> Unit,
) {
    // 탭 목록: 전체 + 리액션이 있는 감정 탭
    val visibleTypes = EmotionType.entries.filter { (countByEmotion[it] ?: 0) > 0 }
    val tabs = remember(visibleTypes) {
        listOf(EmotionTab.All) + visibleTypes.map { EmotionTab.Emotion(it) }
    }

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    // pager의 현재 페이지로 탭 하이라이트 (스와이프 중에도 즉시 반영)
    val displayedTab = tabs.getOrElse(pagerState.currentPage) { EmotionTab.All }

    // pager 스와이프 완료 → 데이터 로드 트리거
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            val tab = tabs.getOrNull(page) ?: return@collect
            onTabSelected(tab)
        }
    }

    Column(
        modifier = modifier
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        // 제목
        SheetTitle()

        Spacer(modifier = Modifier.height(20.dp))

        // 탭 바 — pager 현재 페이지 기준으로 하이라이트
        EmotionTabBar(
            totalCount = totalCount,
            countByEmotion = countByEmotion,
            selectedTab = displayedTab,
            onTabSelected = { tab ->
                onTabSelected(tab)
                val index = tabs.indexOf(tab)
                if (index >= 0) {
                    scope.launch { pagerState.animateScrollToPage(index) }
                }
            },
            pagerState = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 친구 목록 — HorizontalPager로 스와이프 전환, 각 페이지가 자신의 탭 데이터를 직접 참조
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { pageIndex ->
            val pageTab = tabs.getOrNull(pageIndex) ?: EmotionTab.All
            val tabKey = pageTab.toApiKey() ?: "ALL"
            val pageFriends = friendsByTab[tabKey] ?: emptyList()
            val pageHasNext = hasNextByTab[tabKey] ?: false
            val pageLastReactionId = lastReactionIdByTab[tabKey]

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                items(
                    items = pageFriends,
                    key = { "${tabKey}_${it.id}" },
                ) { friend ->
                    EmotionFriendItem(friend = friend)
                }

                // 추가 로드 트리거
                if (pageHasNext && pageLastReactionId != null) {
                    item(key = "${tabKey}_load_more") {
                        LaunchedEffect(pageLastReactionId) {
                            onLoadMore(tabKey, pageLastReactionId)
                        }
                    }
                }

                // 하단 여백
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        .let { Spacer(modifier = Modifier.height(it)) }
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────────
// Title
// ────────────────────────────────────────────────────────────

@Composable
private fun SheetTitle() {
    Text(
        text = "감정 남긴 친구 목록",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = (18 * 1.2).sp,
        color = ColorNeutral900,
        modifier = Modifier.padding(horizontal = 20.dp),
    )
}

// ────────────────────────────────────────────────────────────
// Tab Bar
// ────────────────────────────────────────────────────────────

@Composable
private fun EmotionTabBar(
    totalCount: Int,
    countByEmotion: Map<EmotionType, Int>,
    selectedTab: EmotionTab,
    onTabSelected: (EmotionTab) -> Unit,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val visibleTypes = EmotionType.entries.filter { (countByEmotion[it] ?: 0) > 0 }
    val tabCount = 1 + visibleTypes.size  // 전체 + 감정 탭들
    val density = LocalDensity.current

    // 각 탭의 x좌표와 너비를 저장 (observable) — tabCount 변경 시 초기화
    val tabOffsets = remember(tabCount) { androidx.compose.runtime.mutableStateMapOf<Int, Float>() }
    val tabWidths = remember(tabCount) { androidx.compose.runtime.mutableStateMapOf<Int, Float>() }

    Box(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.Start,
        ) {
            // 전체 탭
            EmotionTabItem(
                label = "전체",
                count = totalCount,
                isSelected = selectedTab is EmotionTab.All,
                onClick = { onTabSelected(EmotionTab.All) },
                modifier = Modifier.onGloballyPositioned { coords ->
                    tabOffsets[0] = coords.positionInParent().x
                    tabWidths[0] = coords.size.width.toFloat()
                },
            )

            // 리액션이 있는 감정 탭만 표시
            visibleTypes.forEachIndexed { index, type ->
                val tabIndex = index + 1
                val tabIconRes = when (type) {
                    EmotionType.SUN -> R.drawable.sun_tab
                    EmotionType.LIKE -> R.drawable.great_tab
                    EmotionType.PARTY -> R.drawable.amazing_tab
                    EmotionType.FIRE -> R.drawable.fighting_tab
                }
                EmotionTabItem(
                    iconRes = tabIconRes,
                    count = countByEmotion[type] ?: 0,
                    isSelected = selectedTab is EmotionTab.Emotion &&
                            (selectedTab as EmotionTab.Emotion).type == type,
                    onClick = { onTabSelected(EmotionTab.Emotion(type)) },
                    modifier = Modifier.onGloballyPositioned { coords ->
                        tabOffsets[tabIndex] = coords.positionInParent().x
                        tabWidths[tabIndex] = coords.size.width.toFloat()
                    },
                )
            }
        }

        // 슬라이딩 인디케이터 바
        if (tabOffsets.size == tabCount && tabWidths.size == tabCount) {
            val currentPage = pagerState.currentPage.coerceIn(0, tabCount - 1)
            val fraction = pagerState.currentPageOffsetFraction

            val targetPage = if (fraction > 0) {
                (currentPage + 1).coerceAtMost(tabCount - 1)
            } else if (fraction < 0) {
                (currentPage - 1).coerceAtLeast(0)
            } else {
                currentPage
            }
            val absFraction = kotlin.math.abs(fraction)

            val currentOffset = tabOffsets[currentPage] ?: 0f
            val targetOffset = tabOffsets[targetPage] ?: 0f
            val currentWidth = tabWidths[currentPage] ?: 0f
            val targetWidth = tabWidths[targetPage] ?: 0f

            val indicatorOffset = currentOffset + (targetOffset - currentOffset) * absFraction
            val indicatorWidth = currentWidth + (targetWidth - currentWidth) * absFraction

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = with(density) { indicatorOffset.toDp() })
                    .width(with(density) { indicatorWidth.toDp() })
                    .height(2.dp)
                    .background(ColorNeutral800),
            )
        }
    }
}

@Composable
private fun EmotionTabItem(
    label: String? = null,
    @androidx.annotation.DrawableRes iconRes: Int? = null,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textColor = if (isSelected) ColorNeutral800 else ColorNeutral400

    Column(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .height(40.dp)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (iconRes != null) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            } else if (label != null) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = textColor,
                )
            }
            Text(
                text = count.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = textColor,
            )
        }
    }
}

// ────────────────────────────────────────────────────────────
// Friend Item Row
// ────────────────────────────────────────────────────────────

private val ItemRowHeight = 44.dp

@Composable
private fun EmotionFriendItem(
    friend: EmotionFriend,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ItemRowHeight)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // 프로필 + 닉네임
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f),
        ) {
            ProfileImage(imageUrl = friend.profileImageUrl)

            Text(
                text = friend.nickname,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = ColorNeutral800,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 감정 뱃지
        EmotionBadge(emotionType = friend.emotionType)
    }
}

@Composable
private fun ProfileImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(ColorNeutral400.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center,
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "프로필 이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_profile_empty),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
private fun EmotionBadge(
    emotionType: EmotionType,
    modifier: Modifier = Modifier,
) {
    val iconRes = when (emotionType) {
        EmotionType.SUN -> R.drawable.sun
        EmotionType.LIKE -> R.drawable.great
        EmotionType.PARTY -> R.drawable.amazing
        EmotionType.FIRE -> R.drawable.fighting
    }

    Image(
        painter = painterResource(iconRes),
        contentDescription = emotionType.emoji,
        modifier = modifier.size(24.dp),
    )
}
