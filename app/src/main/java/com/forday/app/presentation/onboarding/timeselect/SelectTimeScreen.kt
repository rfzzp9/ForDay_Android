package com.forday.app.presentation.onboarding.timeselect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.button.BottomNextButton
import com.forday.app.core.designsystem.component.layout.OnboardingLayout
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.modifyhobby.screen.HobbyModifyParams
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import com.forday.app.presentation.onboarding.OnboardingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import timber.log.Timber

@Serializable
enum class ScreenMode { ONBOARDING, DEFAULT }

// hobbyInfoId에 따라 아이콘 리소스를 반환하는 함수
fun getHobbyIconResource(hobbyInfoId: Int?): Int {
    return when (hobbyInfoId) {
        1 -> R.drawable.ic_draw
        2 -> R.drawable.ic_health
        3 -> R.drawable.ic_book
        4 -> R.drawable.ic_music
        5 -> R.drawable.ic_running
        6 -> R.drawable.ic_cook
        7 -> R.drawable.ic_cafe
        8 -> R.drawable.ic_movie
        9 -> R.drawable.ic_camera2
        10 -> R.drawable.ic_write
        else -> R.drawable.ic_etc_hobby // 기본값
    }
}

@Composable
fun SelectTimeScreenRoot(
    mode: ScreenMode,
    viewModel: OnboardingViewModel,
    params: HobbyModifyParams?,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    viewModel.logEvent("view_hobby_time_selection_screen")  //취미정보 - 시간 선택 화면 진입
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val shouldAutoAdvance by viewModel.shouldAutoAdvanceFromTime.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    SelectTimeScreen(
        params = params,
        hobby = state.selectedHobbyName,
        hobbyInfoId = state.selectedHobbyId?.toInt(), // hobbyInfoId 추가
        selectedTime = state.selectedMinutes,
        mode = mode,
        onTimeSelected = { minutes ->
            viewModel.logEvent("selected_time_$minutes")
            // ONBOARDING 모드일 때만 즉시 저장
            if (mode == ScreenMode.ONBOARDING) {
                viewModel.saveTime(minutes)
            }
        },
        onNext = { updatedMinutes ->
            if (mode == ScreenMode.DEFAULT) {
                viewModel.modifyHobbyTime(params!!.hobbyId.toLong(), updatedMinutes)
            }
            scope.launch {
                delay(400L)
                onNext()
            }
        },
        onBack = {
            viewModel.logEvent("hobby_time_selection_back_click")
            scope.launch {
                delay(400L)
                onBack()
            }
        },
        viewModel = viewModel,
        shouldAutoAdvance = shouldAutoAdvance
    )
}

@Composable
fun SelectTimeScreen(
    params: HobbyModifyParams?,
    hobby: String?,
    hobbyInfoId: Int?, // hobbyInfoId 파라미터 추가
    selectedTime: Int?,
    mode: ScreenMode,
    onTimeSelected: (Int) -> Unit,
    onNext: (Int) -> Unit,  // 선택된 시간을 전달하도록 변경
    onBack: () -> Unit,
    viewModel: OnboardingViewModel,
    shouldAutoAdvance: Boolean,
) {
    // DEFAULT 모드일 때는 로컬 상태로 관리, ONBOARDING일 때는 ViewModel 상태 사용
    val localSelectedTime = remember(params?.hobbyTimeMinutes, selectedTime) {
        mutableStateOf(
            if (mode == ScreenMode.DEFAULT) {
                params?.hobbyTimeMinutes ?: 0
            } else {
                selectedTime ?: 0
            }
        )
    }

    // 실제 사용할 시간 값
    val currentTime = if (mode == ScreenMode.DEFAULT) {
        localSelectedTime.value
    } else {
        selectedTime ?: 0
    }

    LaunchedEffect(Unit) {
        if (mode == ScreenMode.ONBOARDING && selectedTime != null && selectedTime > 0) {
            viewModel.disableAutoAdvanceFromTime()
        }
    }

    // 선택된 시간에 따라 timeLabel 계산
    val timeLabel = when (currentTime) {
        10 -> "10분"
        20 -> "20분"
        30 -> "30분"
        60 -> "1시간"
        120 -> "2시간"
        else -> if (currentTime >= 60) "${currentTime / 60}시간" else "${currentTime}분"
    }

    OnboardingLayout(
        title = "취미 시간",
        currentStep = 2,
        totalSteps = 5,
        mode = mode,
        onBack = { onBack() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ForDayTheme.color.Neutral50)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp)
            ) {
                // Title
                Text(
                    text = "한 번에 얼마나 할 수 있나요?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForDayTheme.color.Neutral900,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle
                val annotatedString = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = ForDayTheme.color.Secondary003)) {
                        append(hobby)
                    }
                    append("에 투자할 수 있는 시간을 선택해주세요.\n처음엔 짧게 시작하는 게 좋아요. 습관이 되면 자연스럽게 늘어나요!")
                }

                Text(
                    text = annotatedString,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = ForDayTheme.color.Gray800,
                    lineHeight = 19.6.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // mode에 따라 다른 데이터로 HobbyCard 표시
                when (mode) {
                    ScreenMode.DEFAULT -> {
                        // DEFAULT 모드: 로컬 상태 기반으로 표시
                        params?.let {
                            HobbyCard(
                                hobbyName = it.hobbyName,
                                timeLabel = timeLabel,
                                isSelected = true,
                                hobbyInfoId = it.hobbyId, // hobbyInfoId 전달
                                executionCount = it.executionCount,
                                goalDays = it.goalDays
                            )
                        }
                    }
                    ScreenMode.ONBOARDING -> {
                        // ONBOARDING 모드: 기존 데이터 사용
//                        if (currentTime > 0) {
                        hobby?.let {
                            HobbyCard(
                                hobbyName = it,
                                timeLabel = timeLabel,
                                isSelected = true,
                                hobbyInfoId = hobbyInfoId // hobbyInfoId 전달
                            )
//                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "가벼운 시작",
                        style = ForDayTheme.typography.body12,
                        color = ForDayTheme.color.Neutral600
                    )
                    Text(
                        text = "더 몰입",
                        style = ForDayTheme.typography.body12,
                        color = ForDayTheme.color.Neutral600
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TimeSelector(
                    onNext = {
                        if (mode == ScreenMode.ONBOARDING && currentTime > 0) {
                            viewModel.enableAutoAdvanceFromTime() // 실제 다음으로 진행하도록 재활성화
                            onNext(currentTime)
                        }
                    },
                    selectedTime = currentTime,
                    onTimeSelected = { minutes ->
                        if (mode == ScreenMode.DEFAULT) {
                            // DEFAULT 모드: 로컬 상태만 업데이트
                            localSelectedTime.value = minutes
                        } else {
                            // ONBOARDING 모드: ViewModel에 즉시 저장
                            viewModel.enableAutoAdvanceFromTime()
                            //시간 선택 시 재활성화
                            onTimeSelected(minutes)
                        }
                    },
                    shouldAutoAdvance = shouldAutoAdvance
                )

                if (mode == ScreenMode.DEFAULT) {
                    BottomNextButton(
                        text = "변경하기",
                        enabled = currentTime > 0,
                        onNext = { onNext(currentTime) }  // 현재 로컬 상태 값 전달
                    )
                }
            }
        }
    }
}

@Composable
fun HobbyCard(
    hobbyName: String,
    timeLabel: String,
    isSelected: Boolean,
    hobbyInfoId: Int? = null,
    executionCount: Int? = null,
    goalDays: Int? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = Color.Black.copy(alpha = 0.06f),
                ambientColor = Color.Black.copy(alpha = 0.06f)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ForDayTheme.color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon - hobbyInfoId에 따라 동적으로 아이콘 표시
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = getHobbyIconResource(hobbyInfoId)),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Text Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 메타 정보 (executionCount, goalDays가 있으면 함께 표시)
                if (executionCount != null && goalDays != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MetaText(timeLabel)
                        DotSeparator()
                        MetaText("주 ${executionCount}회")
                        DotSeparator()
                        MetaText(if (goalDays == 0) "기간 미지정" else "${goalDays}일")
                    }
                } else {
                    // ONBOARDING 모드: timeLabel만 표시
                    if (timeLabel != "0분") {
                        Text(
                            text = timeLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = ForDayTheme.color.StrongDivider,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = hobbyName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = ForDayTheme.color.Gray800
                )
            }

            // Toggle/Check Icon
            Icon(
                painter = painterResource(id = R.drawable.onoff),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected) ForDayTheme.color.Orange03 else ForDayTheme.color.Gray03
            )
        }
    }
}

@Composable
private fun MetaText(text: String) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal,
        color = ForDayTheme.color.StrongDivider,
        lineHeight = 14.sp
    )
}

@Composable
private fun DotSeparator() {
    Box(
        modifier = Modifier
            .size(2.dp)
            .background(ForDayTheme.color.StrongDivider, CircleShape)
    )
}

@Composable
fun TimeSelector(
    onNext: () -> Unit,
    selectedTime: Int,
    onTimeSelected: (Int) -> Unit,
    shouldAutoAdvance: Boolean
) {
    val timeOptions = listOf(
        10 to "10",
        20 to "20",
        30 to "30",
        60 to "1시간",   // ✅ 60분으로 저장
        120 to "2시간"   // ✅ 120분으로 저장
    )

    val selectedIndex = timeOptions.indexOfFirst { it.first == selectedTime }
    var pendingAutoAdvance by remember { mutableStateOf(false) }
    if (selectedIndex >= 0) {
        pendingAutoAdvance = true
    }
    LaunchedEffect(pendingAutoAdvance, shouldAutoAdvance) {
        if (pendingAutoAdvance && shouldAutoAdvance) {
            onNext()
            pendingAutoAdvance = false
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(ForDayTheme.color.White)
    ) {
        // 배경 레이어 (연속된 막대)
        if (selectedIndex >= 0) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 2.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // 선택된 버튼까지의 배경
                    timeOptions.forEachIndexed { index, _ ->
                        if (index < selectedIndex) {
                            // 선택되기 전 버튼들: 연한 주황색
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(Color(0xFFFFE0CC))  // 연한 주황색
                            )
                        } else if (index == selectedIndex) {
//                            onNext()
                            // 선택된 버튼: 진한 주황색 + 둥근 모서리
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .fillMaxHeight()
                                        .background(Color(0xFFFFE0CC))  // 진한 주황색
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(Color(0xFFEE9449))  // 진한 주황색
                                )
                            }

                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // 텍스트 레이어
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            timeOptions.forEachIndexed { index, (minutes, label) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            onClick = rememberThrottledClick { onTimeSelected(minutes) },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedTime == minutes && (minutes == 10 || minutes == 20 || minutes == 30)) "${minutes}분" else label,
                        fontSize = 14.sp,
                        fontWeight = if (selectedTime == minutes) FontWeight.Bold else FontWeight.Medium,
                        color = when {
                            selectedTime == minutes -> Color.White
                            else -> Color(0xFF9E9E9E)  // 선택 이후: 회색
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimeSelectScreenPreview() {
    ForDayTheme {
        SelectTimeScreen(
            hobby = "독서",
            hobbyInfoId = 3, // 예시: 독서는 id 3
            selectedTime = 30,
            onTimeSelected = {},
            onNext = {},
            onBack = {},
            mode = ScreenMode.ONBOARDING,
            params = null,
            viewModel = hiltViewModel(),
            shouldAutoAdvance = false
        )
    }
}