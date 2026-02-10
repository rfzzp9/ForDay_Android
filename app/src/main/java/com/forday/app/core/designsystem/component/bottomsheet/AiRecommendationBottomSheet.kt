package com.forday.app.core.designsystem.component.bottomsheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dayn.forday.R
import com.forday.app.presentation.home.model.AiRoutineItemState
import kotlinx.coroutines.delay
import kotlin.collections.count
import kotlin.collections.filter
import kotlin.collections.forEach
import kotlin.collections.indices
import kotlin.collections.map

/**
 * AI 추천 BottomSheet 상태
 */
sealed class AiBottomSheetState {
    object Initial : AiBottomSheetState()
    data class Loading(val userName: String, val hobbyName: String) : AiBottomSheetState()
    data class Result(
        val message: String,
        val recommendations: List<RecommendedActivity>,
        val selectedCount: Int = 0
    ) : AiBottomSheetState()
}

/**
 * AI 추천 활동 데이터 모델
 */
data class RecommendedActivity(
    val id: Int,
    val title: String,
    val description: String,
    val isSelected: Boolean = false
)

/**
 * AI 추천 BottomSheet
 * Initial → Loading → Result 순서로 화면 전환
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiRecommendationBottomSheet(
    showBottomSheet: Boolean,
    userName: String = "사용자",
    hobbyName: String,
    onDismiss: () -> Unit,
    aiRecommendData: List<AiRoutineItemState>,
    aiCallCount: Int?,
    onAiRecommendButtonClick: () -> Unit,
    onRecommendationsSelected: (List<RecommendedActivity>) -> Unit = {}
) {
    var currentState by remember { mutableStateOf<AiBottomSheetState>(AiBottomSheetState.Initial) }
    var recommendations by remember { mutableStateOf<List<RecommendedActivity>>(emptyList()) }

    // BottomSheet가 열릴 때마다 Initial 상태로 초기화
    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) {
            currentState = AiBottomSheetState.Initial
            recommendations = emptyList()
        }
    }

    // aiRecommendData가 변경되면 Result 화면으로 전환
    LaunchedEffect(aiRecommendData) {
        if (aiRecommendData.isNotEmpty() && currentState is AiBottomSheetState.Loading) {
            // 서버 데이터를 RecommendedActivity로 변환
            val convertedRecommendations = aiRecommendData.map { item ->
                RecommendedActivity(
                    id = item.routineId,
                    title = item.content,
                    description = item.description,
                    isSelected = false
                )
            }

            recommendations = convertedRecommendations
            currentState = AiBottomSheetState.Result(
                message = "주로 아침독서를 하셨네요!",  // TODO: 필요시 동적으로 변경
                recommendations = convertedRecommendations,
                selectedCount = 0
            )
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            dragHandle = null
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
            ) {
                AnimatedContent(
                    targetState = currentState,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                    },
                    label = "ai_bottomsheet_state"
                ) { state ->
                    when (state) {
                        is AiBottomSheetState.Initial -> {
                            InitialScreen(
                                onAiRecommendClick = {
                                    currentState = AiBottomSheetState.Loading(
                                        userName = userName,
                                        hobbyName = hobbyName
                                    )
                                    onAiRecommendButtonClick()
                                }
                            )
                        }

                        is AiBottomSheetState.Loading -> {
                            LoadingScreen(
                                userName = state.userName,
                                hobbyName = state.hobbyName
                            )
                        }

                        is AiBottomSheetState.Result -> {
                            ResultScreen(
                                message = state.message,
                                recommendations = recommendations,
                                selectedCount = recommendations.count { it.isSelected },
                                aiCallCount = aiCallCount,
                                onToggleSelection = { activityId ->
                                    recommendations = recommendations.map { activity ->
                                        activity.copy(isSelected = activity.id == activityId)
                                    }
                                },
                                onRegenerate = {
                                    // 재생성 버튼 - 다시 로딩 화면으로
                                    currentState = AiBottomSheetState.Loading(
                                        userName = userName,
                                        hobbyName = hobbyName
                                    )
                                    onAiRecommendButtonClick()
                                },
                                onAdd = {  //TODO 추가 버튼을 누르면 콜백 넘겨서 취미활동생성 API호출해야 함
                                    val selectedActivities =
                                        recommendations.filter { it.isSelected }
                                    onRecommendationsSelected(selectedActivities)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private object BottomSheetConstants {
    @Composable
    fun getScreenHeight(): Dp {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp

        return when {
            screenWidth < 360.dp -> 220.dp  // 작은 화면
            screenWidth < 600.dp -> 236.dp  // 일반 폰
            else -> 260.dp  // 태블릿
        }
    }
}

/**
 * Initial 화면 - "AI 추천받기" 버튼
 */
@Composable
private fun InitialScreen(
    onAiRecommendClick: () -> Unit
) {
    val screenHeight = BottomSheetConstants.getScreenHeight()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // 화면 크기별 패딩 계산
    val horizontalPadding = when {
        screenWidth < 360.dp -> 16.dp  // 작은 화면
        screenWidth < 600.dp -> 20.dp  // 일반 폰
        else -> 32.dp  // 태블릿
    }

    val topPadding = when {
        screenWidth < 360.dp -> 32.dp
        else -> 40.dp
    }

    val contentSpacing = when {
        screenWidth < 360.dp -> 20.dp
        else -> 24.dp
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight)  // 고정 높이
            .background(Color.White)
            .padding(
                top = topPadding,
                start = horizontalPadding,
                end = horizontalPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(contentSpacing)
    ) {
        // AI 아이콘
        AiIcon()

        // AI 추천받기 버튼
        Button(
            onClick = onAiRecommendClick,
            modifier = Modifier
                .fillMaxWidth()  // 부모 너비에 맞춤
                .widthIn(max = 400.dp)  // 최대 너비 제한 (태블릿 대응)
                .heightIn(min = 48.dp),  // 최소 터치 영역 보장
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFF1E6)
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(
                horizontal = 40.dp,
                vertical = 12.dp
            )
        ) {
            Text(
                text = "AI 추천받기",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9447),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Loading 화면 - AI 분석 중
 * Figma: 2189-17808
 */
@Composable
private fun LoadingScreen(
    userName: String,
    hobbyName: String
) {
    val loadingMessages = listOf(
        "당신의 취향과 패턴을 분석했어요",
        "AI 가 맞는 활동을 찾아줄게요",
        "활동은 추가로 만들 수 있어요"
    )
    val screenHeight = BottomSheetConstants.getScreenHeight()
    var currentMessageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        // 메시지 순환
        for (index in loadingMessages.indices) {
            currentMessageIndex = index
            delay(2500)
        }
    }

    // 화면 크기별 패딩 계산
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val horizontalPadding = when {
        screenWidth < 360.dp -> 16.dp
        screenWidth < 600.dp -> 20.dp
        else -> 32.dp
    }

    val verticalPadding = when {
        screenWidth < 360.dp -> 32.dp
        else -> 40.dp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()  // 고정 높이 대신 컨텐츠에 맞춤
            .heightIn(screenHeight)  // 최소 높이 보장
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    top = verticalPadding,
                    bottom = verticalPadding,
                    start = horizontalPadding,
                    end = horizontalPadding
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // AI 아이콘
            AiIcon()

            // 애니메이션 점들
            LoadingDots()

            // 텍스트
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)  // 최대 너비 제한 (태블릿 대응)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${userName}의 취미를 분석 중",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "$hobbyName AI 활동을 생성 중이에요.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF3A3A3A),
                    textAlign = TextAlign.Center,
                    lineHeight = 19.6.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 로딩 토스트 메시지 (Crossfade 애니메이션)
 */
@Composable
private fun LoadingToastMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Crossfade(
            targetState = message,
            animationSpec = tween(durationMillis = 300),
            label = "loading_toast"
        ) { currentMessage ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    .background(Color(0xCC000000))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentMessage,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.6.sp
                )
            }
        }
    }
}

/**
 * Result 화면 - AI 추천 결과
 * Figma: 2189-18267
 */
@Composable
private fun ResultScreen(
    message: String,
    recommendations: List<RecommendedActivity>,
    aiCallCount: Int?,
    selectedCount: Int,
    onToggleSelection: (Int) -> Unit,
    onRegenerate: () -> Unit,
    onAdd: () -> Unit
) {
    var showTooltip by remember { mutableStateOf(false) }
    var iconPosition by remember { mutableStateOf(IntOffset.Zero) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 헤더 섹션
        Column(
            modifier = Modifier.width(320.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // AI 아이콘
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AiIcon()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = message,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1E1E),
                            textAlign = TextAlign.Center,
                            lineHeight = 21.6.sp
                        )
                        Box {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "더 맞는 활동을 추천할게요.",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1E1E),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 21.6.sp
                                )

                                Icon(
                                    painter = painterResource(id = R.drawable.ic_tooltip),
                                    contentDescription = "정보",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .onGloballyPositioned { coordinates ->
                                            iconPosition = IntOffset(
                                                coordinates.positionInWindow().x.toInt(),
                                                coordinates.positionInWindow().y.toInt()
                                            )
                                        }
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            showTooltip = !showTooltip
                                        },
                                    tint = Color(0xFFB5B5B5)
                                )

                            }

                            if (showTooltip) {
                                Popup(
                                    alignment = Alignment.TopCenter,
                                    offset = IntOffset(
                                        x = 0,
                                        y = iconPosition.y - with(LocalDensity.current) {
                                            1.dp.toPx().toInt()
                                        }
                                    ),
                                    onDismissRequest = { showTooltip = false }
                                ) {
                                    TooltipBubble(
                                        text = "사용자의 취미취향과 다른 유저의\n데이터 기반 추천을 기반하여\n선별된 포데이 AI 추천 취미활동입니다.",
                                        onDismiss = { showTooltip = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 추천 카드 리스트
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                recommendations.forEach { activity ->
                    RecommendationCard(
                        activity = activity,
                        onToggle = { onToggleSelection(activity.id) }
                    )
                }
            }
        }

        // 하단 버튼 영역
        BottomButtonSection(
            selectedCount = selectedCount,
            aiCallCount = aiCallCount,
            onRegenerate = onRegenerate,
            onAdd = onAdd
        )
    }
}

/**
 * 검은 말풍선 툴팁
 */
@Composable
private fun TooltipBubble(
    text: String,
    onDismiss: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .wrapContentWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
    ) {
        // 말풍선 본체
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .background(
                    color = Color(0xFF3A3A3A),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                lineHeight = 16.8.sp,
                textAlign = TextAlign.Center
            )
        }

        // 꼬리 (삼각형)
        Canvas(
            modifier = Modifier
                .size(width = 12.dp, height = 6.dp)
        ) {
            val path = Path().apply {
                moveTo(size.width / 2, 0f)
                lineTo(0f, size.height)
                lineTo(size.width, size.height)
                close()
            }
            drawPath(
                path = path,
                color = Color(0xFF3A3A3A)
            )
        }
    }
}

/**
 * AI 아이콘 (공통)
 */
@Composable
private fun AiIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_ai),
        contentDescription = "AI",
        modifier = Modifier.size(42.dp),
        tint = Color.Unspecified
    )
}

/**
 * 로딩 애니메이션 점들
 */
@Composable
private fun LoadingDots() {
    val loadingComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading_dots)
    )
    val loadingProgress by animateLottieCompositionAsState(
        composition = loadingComposition,
        iterations = LottieConstants.IterateForever
    )

    if (loadingComposition != null) {
        LottieAnimation(
            composition = loadingComposition,
            progress = { loadingProgress },
            modifier = Modifier.heightIn(min = 10.dp)
        )
    }
}

/**
 * 추천 활동 카드
 */
@Composable
private fun RecommendationCard(
    activity: RecommendedActivity,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 타이틀 영역
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activity.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E1E1E),
                        lineHeight = 19.09.sp
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "수정",
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFFB5B5B5)
                    )
                }

                // 토글 버튼
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(
                            width = 1.5.dp,
                            color = if (activity.isSelected) Color(0xFF3A3A3A) else Color(0xFFD1D1D1),
                            shape = CircleShape
                        )
                        .background(
                            color = if (activity.isSelected) Color(0xFF3A3A3A) else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onToggle()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (activity.isSelected) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = "선택됨",
                            modifier = Modifier.size(12.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            // 설명
            Text(
                text = activity.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF7A7A7A),
                lineHeight = 19.6.sp
            )
        }
    }
}

/**
 * 하단 버튼 섹션
 */
@Composable
private fun BottomButtonSection(
    selectedCount: Int,
    aiCallCount: Int?,
    onRegenerate: () -> Unit,
    onAdd: () -> Unit
) {
    val isRegenerateEnabled = (aiCallCount ?: 0) < 3

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x00F9F9F9),
                        Color(0xFFF9F9F9)
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 재생성 버튼
            Button(
                onClick = onRegenerate,
                modifier = Modifier
                    .size(56.dp),
                enabled = isRegenerateEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    disabledContainerColor = Color.White
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFD1D1D1)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_reload),
                        contentDescription = "재생성",
                        modifier = Modifier.size(20.dp),
                        tint = if (isRegenerateEnabled) Color(0xFF3A3A3A) else Color(0xFFB5B5B5)
                    )
                    Text(
                        text = "${aiCallCount ?: 0}/3",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (isRegenerateEnabled) Color(0xFF7A7A7A) else Color(0xFFB5B5B5),
                        lineHeight = 16.8.sp
                    )
                }
            }

            // 추가 버튼
            Button(
                onClick = onAdd,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCount > 0) Color(0xFFFF9447) else Color(0xFFFFE6D1),
                    disabledContainerColor = Color(0xFFFFE6D1)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                enabled = selectedCount > 0
            ) {
                Text(
                    text = "추가",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 16.8.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InitialScreenPreview() {
    InitialScreen(onAiRecommendClick = {})
}


@Preview(showBackground = true)
@Composable
private fun LoadingScreenPreview() {
    LoadingScreen(userName = "유지2", hobbyName = "독서")
}

@Preview(showBackground = true)
@Composable
private fun ResultScreenPreview() {
    ResultScreen(
        message = "주로 아침독서를 하셨네요!",
        recommendations = listOf(
            RecommendedActivity(1, "책 5 페이지 읽기", "끝이 정해진 독서라서, 시작이 가볍습니다.", false),
            RecommendedActivity(2, "문단 1개 소리내서 읽기", "소리 내어 읽으면 생각보다 마음이 빨리 가라앉아요.", true),
            RecommendedActivity(3, "출근길 독서", "아침 출근길을 알차게 써보는 건 어떠세요?", false)
        ),
        selectedCount = 1,
        onToggleSelection = {},
        onRegenerate = {},
        onAdd = {},
        aiCallCount = 0
    )
}