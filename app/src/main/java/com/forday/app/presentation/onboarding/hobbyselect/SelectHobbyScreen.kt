package com.forday.app.presentation.onboarding.hobbyselect

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.button.BottomButtonState
import com.forday.app.core.designsystem.component.button.BottomNextButton
import com.forday.app.core.designsystem.component.layout.OnboardingLayout
import com.forday.app.core.designsystem.dialog.HobbyInputDialog
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.onboarding.OnboardingViewModel

@Composable
fun SelectHobbyScreenRoot(
    onNext: () -> Unit,
    onBack: () -> Unit,
    viewModel: OnboardingViewModel,
    fromModifyHobbyOrHome: Boolean = false,
) {
    viewModel.logEvent("select_hobby_screen") // 취미카드 선택화면 진입
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(fromModifyHobbyOrHome) {
        if (fromModifyHobbyOrHome) {
            viewModel.resetOnboardingState()
            viewModel.getHobbyCardDataAgain()
        } else {
            viewModel.fetchHobbyData()
        }
    }

    SelectHobbyScreen(
        hobbies = state.hobbies,
        selectedHobbyId = state.selectedHobbyId,
        customHobbyText = state.customHobbyText,
        showCustomHobbyDialog = state.showDialog,
        onHobbySelected = { hobbyId, hobbyName ->
            viewModel.logEvent("selected_hobby_card_${hobbyName}")  // 선택한 취미 카드
            viewModel.saveHobbyInfo(hobbyId, hobbyName)
        },
        onShowCustomDialog = {
            viewModel.logEvent("click_direct_input_hobby_btn")  // 직접 입력하기 버튼 클릭
            viewModel.showDialog()
        },
        onDismissCustomDialog = {
            viewModel.dismissDialog()
        },
        onCustomHobbyConfirm = { text ->
            viewModel.logEvent("hobby_user_custom_$text")  // 사용자가 입력한 취미 :
            viewModel.confirmCustomHobby(text)
            onNext()
        },
        onNext = {
            onNext()
        },
        onBack = {
            onBack()
        }
    )
}

@Composable
fun SelectHobbyScreen(
    hobbies: List<Hobby>,
    selectedHobbyId: Long?,
    customHobbyText: String,
    showCustomHobbyDialog: Boolean,
    onHobbySelected: (Long?, String) -> Unit,
    onShowCustomDialog: () -> Unit,
    onDismissCustomDialog: () -> Unit,
    onCustomHobbyConfirm: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    OnboardingLayout(
        title = "취미 선택",
        currentStep = 1,
        totalSteps = 5,
        onBack = onBack
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ForDayTheme.color.Neutral50)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 8.dp,
                        bottom = 100.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    item(span = { GridItemSpan(2) }) {
                        HobbySelectTitle()
                    }

                    items(hobbies) { hobby ->
                        HobbyCard(
                            hobby = hobby,
                            isSelected = hobby.id == selectedHobbyId,
                            onClick = { onHobbySelected(hobby.id, hobby.name) },
                        )
                    }

                    item(span = { GridItemSpan(2) }) {
                        AddCustomHobbyButton(
                            customHobbyText = customHobbyText,
                            onClick = onShowCustomDialog
                        )
                    }
                }
            }

            // 다음 버튼 - 상태에 따라 ENABLED/DISABLED 전환
            BottomNextButton(
                text = "다음",
                state = if (selectedHobbyId != null || customHobbyText.isNotEmpty()) {
                    BottomButtonState.ENABLED
                } else {
                    BottomButtonState.DISABLED
                },
                onClick = onNext,
                modifier = Modifier.align(Alignment.BottomCenter).background(ForDayTheme.color.Neutral50)
            )

            if (showCustomHobbyDialog) {
                HobbyInputDialog(
                    title = "취미 입력",
                    description = "취미를 입력해 주세요.",
                    onDismiss = onDismissCustomDialog,
                    onNext = onCustomHobbyConfirm
                )
            }
        }
    }
}

@Composable
private fun HobbySelectTitle() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "어떤 취미를 시작하고 싶으세요?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E),
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = buildAnnotatedString {
                append("마음에 드는 취미를 ")
                pushStyle(SpanStyle(color = Color(0xFFF25F59)))
                append("1개")
                pop()
                append(" 선택해주세요.\n")
                append("취미슬롯은 1개 더 확장 가능해요!")
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF3A3A3A),
            lineHeight = 19.6.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun HobbyCard(
    hobby: Hobby,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFFF4A261) else Color(0xFFE5E5E5),
                shape = RoundedCornerShape(16.dp)
            )
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        // Image section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = hobby.imageResId),
                contentDescription = hobby.name,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .padding(bottom = 14.dp)
                    .padding(horizontal = 48.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )

            HobbyCheckbox(
                isSelected = isSelected,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 9.dp, end = 11.dp)
            )
        }

        // Text section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 9.dp, vertical = 8.dp)
        ) {
            Text(
                text = hobby.name,
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color(0xFF1E1E1E)
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = hobby.description,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 16.8.sp,
                    color = Color(0xFF7A7A7A)
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun HobbyCheckbox(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(22.dp)
            .background(
                color = if (isSelected) Color(0xFFEE9449) else Color.White,
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else Color(0xFFD1D1D1),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = "선택됨",
                modifier = Modifier.size(12.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun AddCustomHobbyButton(customHobbyText: String, onClick: () -> Unit) {
    val isCustomHobbySelected = customHobbyText.isNotEmpty()

    Column {
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                width = 1.dp,
                color = if (isCustomHobbySelected) {
                    ForDayTheme.color.Primary001  // 선택 시 주황색 border
                } else {
                    ForDayTheme.color.Gray03
                }
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_pen),
                    contentDescription = "취미 입력 버튼",
                    modifier = Modifier.size(20.dp),
                    tint = ForDayTheme.color.Neutral600
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = if (customHobbyText.isEmpty()) {
                        "원하는 취미가 없으신가요?"
                    } else {
                        customHobbyText
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForDayTheme.color.Neutral600,
                )
                Spacer(modifier = Modifier.width(10.dp))
                // 체크 아이콘 (선택되었을 때만 표시)
                if (isCustomHobbySelected) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = "선택됨",
                        modifier = Modifier
                            .size(16.dp),
                        tint = Color(0xFFEE9449)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun SelectHobbyScreenPreview() {
    ForDayTheme {
        SelectHobbyScreen(
            hobbies = listOf(
                Hobby(1, "독서", "책 읽기", R.drawable.ic_launcher_foreground),
                Hobby(2, "운동", "헬스", R.drawable.ic_launcher_foreground),
            ),
            selectedHobbyId = 0,
            customHobbyText = "피아노",
            showCustomHobbyDialog = false,
            onHobbySelected = { _, _ -> Unit },
            onShowCustomDialog = {},
            onDismissCustomDialog = {},
            onCustomHobbyConfirm = {},
            onNext = {},
            onBack = {}
        )
    }
}