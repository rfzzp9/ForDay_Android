package com.forday.app.presentation.mypage.routinedetail

import androidx.activity.compose.BackHandler
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.forday.app.core.designsystem.theme.ForDayTheme
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.forday.app.presentation.mypage.MyPageViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.button.BottomButtonState
import com.forday.app.core.designsystem.component.button.BottomNextButton
import com.forday.app.domain.model.ReactionDetailDomain
import com.forday.app.domain.model.ReactionUserInfo
import com.forday.app.presentation.mypage.MyPageUiState

// 색상 정의
object ActivityDetailColors {
    val Neutral900 = Color(0xFF1E1E1E)
    val Neutral800 = Color(0xFF3A3A3A)
    val Neutral600 = Color(0xFF7A7A7A)
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val Background001 = Color(0xFFFFFFFF)
    val Background002 = Color(0xFFF9F9F9)
    val Stroke001 = Color(0xFFE5E5E5)
    val Action001 = Color(0xFFFF9447)  // Primary orange
    val ToastBackground = Color(0xAD000000)  // 68% black
    val ToastSuccess = Color(0xFFD9F7E5)
}

data class ReactionDetailUiModel(
    val isSuccess: Boolean = false,
    val reactionType: String = "",
    val users: List<ReactionUserUiModel> = emptyList(),
    val message: String = "",
    val errorClassName: String = ""
)

data class ReactionUserUiModel(
    val userId: String = "",
    val nickname: String = "",
    val profileImageUrl: String? = "",
    val reactedAt: String = ""
)

fun ReactionDetailDomain.toUiModel() =
    ReactionDetailUiModel(
        isSuccess = isSuccess,
        reactionType = reactionType,
        users = users.map { it.toUiModel() },
        message = message,
        errorClassName = errorClassName
    )

fun ReactionUserInfo.toUiModel(): ReactionUserUiModel =
    ReactionUserUiModel(
        userId = userId,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        reactedAt = reactedAt
    )

// 반응 타입
enum class ReactionType {
    AWESOME,    // 멋져요
    GREAT,      // 최고예요
    AMAZING,    // 놀라워요
    FIGHTING    // 응원해요
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(
    onBackClick: () -> Unit = {},
    routineId: Long,
    onNavigateToMyPage: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},  //
    onMoreMenuClick: () -> Unit = {},
    isNewRecord: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel,
    onNavigateToRecordRoutine: (RoutineRecordDetailUiModel?, Boolean) -> Unit,
) {
    BackHandler(enabled = isNewRecord) {
        onNavigateToHome()
    }
    Timber.e("routineId2@@@@@@@@@@@@@ : "+routineId)
    LaunchedEffect(Unit) {
        viewModel.getMyRoutineRecordDetail(routineId.toInt())
        viewModel.getNickname()
    }
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val routine = state.value.myRoutineDetails
    val isBookmarked = state.value.isScraped ?: routine?.isScraped ?: false


    // Optimistic Update용 임시 상태
    var selectedReactions by remember { mutableStateOf<Set<ReactionType>>(emptySet()) }
    var canceledReactions by remember { mutableStateOf<Set<ReactionType>>(emptySet()) }
    var pendingReaction by remember { mutableStateOf<ReactionType?>(null) }
    var showReactionUsers by remember { mutableStateOf(false) }
    var displayedReaction by remember { mutableStateOf<ReactionType?>(null) }
    var reactionListJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    var showMoreMenu by remember { mutableStateOf(false) }
    var moreIconBottomPx by remember { mutableFloatStateOf(0f) }
    var containerTopPx by remember { mutableFloatStateOf(0f) }
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var showPrivacyBottomSheet by remember { mutableStateOf(false) }
    var selectedPrivacy by remember { mutableStateOf(routine?.isPublic) }

    //
    var showSuccessAnimation by remember { mutableStateOf(isNewRecord) }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    //
    LaunchedEffect(showSuccessAnimation) {
        if (showSuccessAnimation) {
            delay(3000)
            showSuccessAnimation = false
        }
    }

    Timber.e("@@@@@@@@@@@@content "+state.value.myRoutineDetails?.content)

    val density = LocalDensity.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ActivityDetailColors.Background001)
            .onGloballyPositioned { coords ->
                containerTopPx = coords.positionInRoot().y
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            RoutineDetailHeader(
                title = "내 활동 보기",
                onBackClick = onBackClick,
                onMoreMenuClick = { showMoreMenu = !showMoreMenu },
                isNewRecord = isNewRecord,
                onMoreIconPositioned = { bottomPx -> moreIconBottomPx = bottomPx }
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ActivityContent(routine = routine)
            }

            //
            if (isNewRecord) {
                //
                BottomNextButton(
                    text = "홈으로 가기",
                    state = BottomButtonState.ENABLED,
                    onClick = onNavigateToHome
                )
            } else {
                //
                AnimatedVisibility(
                    visible = showReactionUsers,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    ReactionUsersList(
                        reactionType = displayedReaction ?: ReactionType.AWESOME,
                        users = state.value.reactionUsers.users
                    )
                }

                // Bottom Reaction Bar
                BottomReactionBar(
                    selectedReactions = selectedReactions,
                    canceledReactions = canceledReactions,
                    myReactions = routine?.myReactions,
                    reactions = routine?.reactions,
                    isBookmarked = isBookmarked,
                    onBookmarkClick = {
                        if (isBookmarked) {
                            // 북마크 취소
                            viewModel.cancelScrapPosting(routineId.toInt())
                        } else {
                            // 북마크 추가
                            viewModel.scrapPosting(routineId.toInt())
                        }
                    },
                    onReactionClick = { reaction ->
                        if (showReactionUsers) {
                            if (pendingReaction == reaction) {
                                pendingReaction = null
                                showReactionUsers = false
                            } else {
                                // 다른 버튼 탭 → 목록 전환 + API 호출
                                pendingReaction = reaction
                                displayedReaction = reaction
                                val reactionString = when (reaction) {
                                    ReactionType.AWESOME -> "AWESOME"
                                    ReactionType.GREAT -> "GREAT"
                                    ReactionType.AMAZING -> "AMAZING"
                                    ReactionType.FIGHTING -> "FIGHTING"
                                }
                                viewModel.getReactionUsers(routineId.toInt(), reactionString, "", 10)
                            }
                        } else {
                            if (reactionListJob != null && pendingReaction == reaction) {
                                reactionListJob?.cancel()
                                reactionListJob = null
                                pendingReaction = null

                                val isPressed = when (reaction) {
                                    ReactionType.AWESOME -> routine?.myReactions?.pressedAwesome
                                    ReactionType.GREAT -> routine?.myReactions?.pressedGreat
                                    ReactionType.AMAZING -> routine?.myReactions?.pressedAmazing
                                    ReactionType.FIGHTING -> routine?.myReactions?.pressedFighting
                                }
                                val isCurrentlySelected = (isPressed == true && !canceledReactions.contains(reaction))
                                        || selectedReactions.contains(reaction)
                                val reactionString = when (reaction) {
                                    ReactionType.AWESOME -> "AWESOME"
                                    ReactionType.GREAT -> "GREAT"
                                    ReactionType.AMAZING -> "AMAZING"
                                    ReactionType.FIGHTING -> "FIGHTING"
                                }
                                if (isCurrentlySelected) {
                                    if (isPressed == true) {
                                        viewModel.cancelMyReaction(routineId.toInt(), reactionString)
                                        canceledReactions = canceledReactions + reaction
                                    }
                                    selectedReactions = selectedReactions - reaction
                                } else {
                                    viewModel.reactionToRoutinePosting(routineId.toInt(), reactionString)
                                    selectedReactions = selectedReactions + reaction
                                    canceledReactions = canceledReactions - reaction
                                }
                            } else {
                                // 첫 탭 → 타이머 시작 + API 호출
                                reactionListJob?.cancel()
                                pendingReaction = reaction

                                val reactionString = when (reaction) {
                                    ReactionType.AWESOME -> "AWESOME"
                                    ReactionType.GREAT -> "GREAT"
                                    ReactionType.AMAZING -> "AMAZING"
                                    ReactionType.FIGHTING -> "FIGHTING"
                                }
                                viewModel.getReactionUsers(routineId.toInt(), reactionString, "", 10)

                                reactionListJob = scope.launch {
                                    delay(300L)
                                    displayedReaction = reaction
                                    showReactionUsers = true
                                    reactionListJob = null
                                }
                            }
                        }
                    }
                )
            }
        }

        // More Menu Dropdown
        if (showMoreMenu) {
            MoreMenuDropdown(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = with(density) { (moreIconBottomPx - containerTopPx).toDp() } + 8.dp,
                        end = 20.dp
                    )
                    .zIndex(10f),
                onModifyPosting = {
                    showMoreMenu = false
                    onNavigateToRecordRoutine(state.value.myRoutineDetails, true)
                },
                onSetThumbnailClick = {
                    viewModel.setHobbyMainImage(state.value.myRoutineDetails?.hobbyId?.toLong(), null, state.value.myRoutineDetails?.recordId?.toLong())
                    showMoreMenu = false
                    toastMessage = "대표사진 설정 완료!"
                    showToast = true
                    scope.launch {
                        delay(3000)
                        showToast = false
                    }
                    onNavigateToMyPage()
                },
                onDeletePosting = {
                    showMoreMenu = false
                    routine?.recordId?.let { recordId ->
                        viewModel.deletePosting(recordId.toLong())
                    }
                    onBackClick()
                },
                state = state.value,
            )
        }

        // Toast Message
        AnimatedVisibility(
            visible = showToast,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 112.dp)
        ) {
            ToastMessage2(message = toastMessage)
        }

        // ✅ 기록 완료 Lottie 애니메이션 (버튼과 함께 표시)
        AnimatedVisibility(
            visible = showSuccessAnimation,
            enter = fadeIn(animationSpec = tween(250)),
            exit = fadeOut(animationSpec = tween(250)),
            modifier = Modifier.fillMaxSize()
        ) {
            RecordSuccessAnimation(state.value.userInfo?.nickName ?: "포비")
        }
    }

    // Privacy Setting Bottom Sheet
    if (showPrivacyBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPrivacyBottomSheet = false },
            sheetState = sheetState,
            containerColor = ActivityDetailColors.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            dragHandle = null
        ) {
            PrivacySettingBottomSheet(
                selectedPrivacy = selectedPrivacy == true,
                onPrivacySelected = { privacy ->
                    selectedPrivacy = privacy
                },
                onConfirmClick = {
                    showPrivacyBottomSheet = false
                    toastMessage = "공개범위가 변경되었습니다"
                    showToast = true
                    scope.launch {
                        delay(3000)
                        showToast = false
                    }
                }
            )
        }
    }
}

@Composable
fun ReactionUserItem(user: ReactionUserUiModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.width(48.dp)
    ) {
        // 프로필 이미지
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ActivityDetailColors.Stroke001),
            contentAlignment = Alignment.Center
        ) {
            if (!user.profileImageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = user.profileImageUrl,
                    contentDescription = user.nickname,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_profile_empty),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.Unspecified
                )
            }
        }

        // 닉네임
        Text(
            text = user.nickname,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            color = ActivityDetailColors.Neutral600,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ReactionUsersList(
    reactionType: ReactionType,
    users: List<ReactionUserUiModel>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ActivityDetailColors.Background001,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            users.forEach { user ->
                ReactionUserItem(user = user)
            }
        }
    }
}

@Composable
fun RoutineDetailHeader(
    title: String,
    onBackClick: () -> Unit,
    onMoreMenuClick: () -> Unit,
    isNewRecord: Boolean = false,
    onMoreIconPositioned: (Float) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ✅ isNewRecord가 true면 뒤로가기 버튼 숨김
        if (isNewRecord) {
            Spacer(modifier = Modifier.size(24.dp))
        } else {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_chevron_left),
                    contentDescription = "뒤로가기",
                    tint = ActivityDetailColors.Neutral800
                )
            }
        }

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ActivityDetailColors.Neutral800
        )

        // ✅ isNewRecord가 true면 더보기 버튼도 숨김
        if (isNewRecord) {
            Spacer(modifier = Modifier.size(24.dp))
        } else {
            IconButton(
                onClick = onMoreMenuClick,
                modifier = Modifier
                    .size(24.dp)
                    .onGloballyPositioned { coords ->
                        onMoreIconPositioned(coords.positionInRoot().y + coords.size.height)
                    }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = "더보기",
                    tint = ActivityDetailColors.Neutral800
                )
            }
        }
    }
}

@Composable
fun ActivityContent(routine: RoutineRecordDetailUiModel?) {
    val hasNoImageAndMemo = routine?.imageUrl.isNullOrEmpty() && routine?.memo.isNullOrEmpty()

    // stickerUrl을 drawable 리소스로 변환
    val stickerDrawableRes = remember(routine?.stickerUrl) {
        when {
            routine?.stickerUrl?.contains("smile", ignoreCase = true) == true -> R.drawable.ic_sticker_smile
            routine?.stickerUrl?.contains("laugh", ignoreCase = true) == true -> R.drawable.ic_sticker_laugh
            routine?.stickerUrl?.contains("sad", ignoreCase = true) == true -> R.drawable.ic_sticker_sad
            routine?.stickerUrl?.contains("angry", ignoreCase = true) == true -> R.drawable.ic_sticker_angry
            else -> null
        }
    }

    val painter = rememberAsyncImagePainter(model = routine?.imageUrl)
    val imageState by painter.state.collectAsState()  // ✅ collectAsState로 상태 관찰 → recomposition 트리거

    val imageAspectRatio = if (imageState is AsyncImagePainter.State.Success) {
        val image = (imageState as AsyncImagePainter.State.Success).result.image
        val width = image.width.toFloat()
        val height = image.height.toFloat()
        if (height > 0f) width / height else 1f
    } else {
        1f
    }

    if (hasNoImageAndMemo) {
        // 이미지와 메모가 모두 없을 때 - 스티커만 표시
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // 타이틀과 날짜
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                routine?.content?.let {
                    Text(
                        text = it,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ActivityDetailColors.Neutral900,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                routine?.date?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ActivityDetailColors.Neutral600
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 스티커 - 우측 정렬 (drawable 리소스 사용)
            stickerDrawableRes?.let { drawableRes ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 40.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Image(
                        painter = painterResource(id = drawableRes),
                        contentDescription = "스티커",
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    } else {
        // 기존 레이아웃 (이미지 또는 메모가 있을 때)
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Title
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = routine.content,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ActivityDetailColors.Neutral900,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )


//                Spacer(modifier = Modifier.height(24.dp))

                if (routine.imageUrl.isNullOrEmpty()) {
                    Text(
                        text = routine.date,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ActivityDetailColors.Neutral600
                    )
                }
            }

            // Image and Details
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image - imageUrl이 있을 때만 표시
                if (!routine.imageUrl.isNullOrEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(imageAspectRatio)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    1.dp,
                                    ActivityDetailColors.Stroke001,
                                    RoundedCornerShape(16.dp)
                                )
                                .background(ActivityDetailColors.White)
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = "활동 이미지",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )

                            stickerDrawableRes?.let { drawableRes ->
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .align(Alignment.BottomEnd)
                                        .padding(end = 21.dp, bottom = 21.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = drawableRes),
                                        contentDescription = "스티커",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }

                        // Timestamp
                        Text(
                            text = routine.date,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = ActivityDetailColors.Neutral600
                        )
                    }
                }
                Timber.e("@@@@@@@@@@@@@@############# @#@#@#@ "+routine.memo)
                // Memo Content
                if (!routine.memo.isNullOrEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        shape = RoundedCornerShape(12.dp),
                        color = ActivityDetailColors.Background002
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = routine.memo,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 19.6.sp,
                                color = Color(0xFF3A3A3A),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        bottom = if (stickerDrawableRes != null) 96.dp else 0.dp
                                    ) // 스티커가 있으면 공간 확보
                            )
                            if (routine.imageUrl.isNullOrEmpty()) {  // TODO 여기 테스트 이미지, 텍스트 둘 다 올리면 텍스트 안나오는 오류
                                // 스티커 - 우측 하단
                                stickerDrawableRes?.let { drawableRes ->
                                    Image(
                                        painter = painterResource(id = drawableRes),
                                        contentDescription = "스티커",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .align(Alignment.BottomEnd),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordSuccessAnimation(
    nickName: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Lottie 애니메이션
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.record_complete)
            )
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = 1,
                isPlaying = true,  // ✅ 명시적으로 재생
                restartOnPlay = true  // ✅ 재시작 허용
            )

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )

            // "기록 완료!" 텍스트
            Text(
                text = "수고했어요, ${nickName}님\n 오늘의 포데이 완료!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}



@Composable
fun BottomReactionBar(
    selectedReactions: Set<ReactionType>,
    canceledReactions: Set<ReactionType>,
    myReactions: RoutineUserReactionUiModel?,
    reactions: RoutineReactionUiModel?,
    onReactionClick: (ReactionType) -> Unit,
    isBookmarked: Boolean,  // ✅ 추가
    onBookmarkClick: () -> Unit  // ✅ 추가
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ActivityDetailColors.Background001,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,  // ✅ SpaceBetween으로 변경
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ 왼쪽: 리액션 버튼들
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Awesome Reaction (멋져요)
                ReactionButton(
                    reactionType = ReactionType.AWESOME,
                    isSelected = selectedReactions.contains(ReactionType.AWESOME),
                    isPressed = myReactions?.pressedAwesome,
                    isCanceled = canceledReactions.contains(ReactionType.AWESOME),
                    hasNewReaction = reactions?.awesome,
                    onClick = { onReactionClick(ReactionType.AWESOME) }
                )

                // Great Reaction (최고예요)
                ReactionButton(
                    reactionType = ReactionType.GREAT,
                    isSelected = selectedReactions.contains(ReactionType.GREAT),
                    isPressed = myReactions?.pressedGreat,
                    isCanceled = canceledReactions.contains(ReactionType.GREAT),
                    hasNewReaction = reactions?.great,
                    onClick = { onReactionClick(ReactionType.GREAT) }
                )

                // Amazing Reaction (놀라워요)
                ReactionButton(
                    reactionType = ReactionType.AMAZING,
                    isSelected = selectedReactions.contains(ReactionType.AMAZING),
                    isPressed = myReactions?.pressedAmazing,
                    isCanceled = canceledReactions.contains(ReactionType.AMAZING),
                    hasNewReaction = reactions?.amazing,
                    onClick = { onReactionClick(ReactionType.AMAZING) }
                )

                // Fighting Reaction (응원해요)
                ReactionButton(
                    reactionType = ReactionType.FIGHTING,
                    isSelected = selectedReactions.contains(ReactionType.FIGHTING),
                    isPressed = myReactions?.pressedFighting,
                    isCanceled = canceledReactions.contains(ReactionType.FIGHTING),
                    hasNewReaction = reactions?.fighting,
                    onClick = { onReactionClick(ReactionType.FIGHTING) }
                )
            }

            // ✅ 오른쪽: 북마크 아이콘 (end에 고정)
            IconButton(
                onClick = onBookmarkClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isBookmarked) {
                            R.drawable.icon_bookmark_selected
                        } else {
                            R.drawable.icon_bookmark_unselected
                        }
                    ),
                    contentDescription = if (isBookmarked) "북마크 해제" else "북마크",
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun ReactionButton(
    reactionType: ReactionType,
    isSelected: Boolean,
    isPressed: Boolean?,
    isCanceled: Boolean,  // ✅ 추가
    hasNewReaction: Boolean?,
    onClick: () -> Unit
) {
    // ✅ 실제 활성 상태 계산: (서버에 있고 취소 안 함) OR 로컬 추가
    val isActive = (isPressed == true && !isCanceled) || isSelected

    // ✅ 상태에 따라 아이콘 변경
    val iconRes = when (reactionType) {
        ReactionType.AWESOME -> if (isActive) {
            R.drawable.ic_cool_selected
        } else {
            R.drawable.ic_cool_unselected
        }
        ReactionType.GREAT -> if (isActive) {
            R.drawable.ic_good_selected
        } else {
            R.drawable.ic_good_unselected
        }
        ReactionType.AMAZING -> if (isActive) {
            R.drawable.ic_excellent_selected
        } else {
            R.drawable.ic_excellent_unselected
        }
        ReactionType.FIGHTING -> if (isActive) {
            R.drawable.ic_fire_selected
        } else {
            R.drawable.ic_fire_unselected
        }
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(ActivityDetailColors.Background002)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = when (reactionType) {
                ReactionType.AWESOME -> "멋져요"
                ReactionType.GREAT -> "최고예요"
                ReactionType.AMAZING -> "놀라워요"
                ReactionType.FIGHTING -> "응원해요"
            },
            modifier = Modifier.fillMaxSize(),
            tint = Color.Unspecified
        )
    }
}

@Composable
fun MoreMenuDropdown(
    modifier: Modifier = Modifier,
    onModifyPosting: () -> Unit,
    onSetThumbnailClick: () -> Unit,
    onDeletePosting: () -> Unit,
    state: MyPageUiState,
) {
    Surface(
        modifier = modifier.wrapContentSize(),
        shape = RoundedCornerShape(12.dp),
        color = ActivityDetailColors.Background001,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Privacy Setting
            if (!state.myRoutineDetails?.imageUrl.isNullOrEmpty()) {
//                MoreMenuItem(  // TODO 추후에 대표사진 설정 기능 다 하면 주석 풀기
//                    icon = painterResource(R.drawable.ic_profile_main),
//                    text = "대표사진 설정",
//                    onClick = onSetThumbnailClick
//                )
            }

            // Set Thumbnail
            MoreMenuItem(
                icon = painterResource(R.drawable.ic_pencil_bold),
                text = "수정하기",
                onClick = onModifyPosting
            )

            MoreMenuItem(
                icon = painterResource(R.drawable.ic_trash_bold),
                text = "삭제하기",
                onClick = onDeletePosting
            )
        }
    }
}

@Composable
fun MoreMenuItem(
    icon: Painter,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = ActivityDetailColors.Neutral800
        )

        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = ActivityDetailColors.Neutral800
        )
    }
}

@Composable
fun ToastMessage2(message: String) {
    Surface(
        modifier = Modifier
            .width(320.dp)
            .height(44.dp),
        shape = RoundedCornerShape(12.dp),
        color = ActivityDetailColors.ToastBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Success Icon
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(ActivityDetailColors.ToastSuccess),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color(0xFF00C853)
                )
            }

            // Message
            Text(
                text = message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ActivityDetailColors.White
            )
        }
    }
}

// 공개범위 설정 바텀시트
@Composable
fun PrivacySettingBottomSheet(
    selectedPrivacy: Boolean, // isPublic
    onPrivacySelected: (Boolean) -> Unit,
    onConfirmClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.width(320.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title
            Text(
                text = "활동 공개범위를 설정해주세요.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ActivityDetailColors.Neutral900,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Privacy Options
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 전체공개
                PrivacyOptionCard(
                    icon = Icons.Default.Lock,
                    title = "전체공개",
                    isSelected = selectedPrivacy == true,
                    onClick = { onPrivacySelected(true) }
                )

                // 비공개
                PrivacyOptionCard(
                    icon = Icons.Default.Lock,
                    title = "비공개",
                    isSelected = selectedPrivacy == false,
                    onClick = { onPrivacySelected(false) }
                )
            }
        }

        // Bottom Button Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
        ) {
            // Gradient Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                ActivityDetailColors.White
                            ),
                            startY = 0f,
                            endY = 88f * 0.61648f
                        )
                    )
            )

            // Confirm Button
            Button(
                onClick = onConfirmClick,
                modifier = Modifier
                    .width(328.dp)
                    .height(56.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ActivityDetailColors.Action001
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "설정완료",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ActivityDetailColors.White
                )
            }
        }
    }
}

@Composable
fun PrivacyOptionCard(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = ActivityDetailColors.White,
        border = if (isSelected) {
            BorderStroke(1.dp, ActivityDetailColors.Action001)
        } else {
            BorderStroke(1.dp, ActivityDetailColors.Stroke001)
        },
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isSelected) ActivityDetailColors.Action001 else ActivityDetailColors.Neutral600
                )

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ActivityDetailColors.Neutral800
                )
            }

            // Check Icon
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "선택됨",
                    modifier = Modifier.size(24.dp),
                    tint = ActivityDetailColors.Action001
                )
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Preview
@Composable
fun PreviewActivityDetailScreen() {
    ForDayTheme {
        // Preview에서는 실제 ViewModel이 필요하므로 생략
    }
}