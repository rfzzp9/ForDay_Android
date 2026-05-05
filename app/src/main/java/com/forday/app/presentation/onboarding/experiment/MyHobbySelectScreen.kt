package com.forday.app.presentation.onboarding.experiment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.dayn.forday.R
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.core.designsystem.theme.Pretendard
import com.forday.app.core.logger.analytics.AnalyticsEvents
import com.forday.app.presentation.onboarding.OnboardingFlowViewModel

@Composable
fun MyHobbySelectRoute(
    onNext: () -> Unit,
    viewModel: OnboardingFlowViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.logEvent(AnalyticsEvents.MY_HOBBY_SELECT_SCREEN)
    }

    MyHobbySelectScreen(
        onBackClick = {},
        onNextClick = { selectedHobbies ->
            viewModel.logEvent(AnalyticsEvents.MY_HOBBY_SELECT_COMPLETE)
            viewModel.saveIsOnboardingCompleted(true)
            onNext()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MyHobbySelectScreen(
    userName: String = "유지",
    onBackClick: () -> Unit = {},
    onNextClick: (List<String>) -> Unit = {},
) {
    val defaultHobbies = listOf(
        "그림 그리기",
        "헬스",
        "독서",
        "음악 듣기",
        "러닝",
        "요가",
        "카페 탐방",
        "영화 보기",
        "사진 촬영",
        "글쓰기",
    )
    val hobbies = remember { mutableStateListOf(*defaultHobbies.toTypedArray()) }
    val selectedHobbies = remember { mutableStateListOf<String>() }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 11.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = Color(0xFF3A3A3A),
                    )
                }
                Text(
                    text = "취미 선택",
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 19.2.sp,
                        color = Color(0xFF3A3A3A),
                    ),
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        },
        containerColor = Color(0xFFF9F9F9),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.main_character),
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "$userName 님,\n어떤 취미를 시작하고 싶으세요?",
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                lineHeight = 24.sp,
                                color = Color(0xFF1E1E1E),
                            ),
                        )
                        Text(
                            text = "마음에 드는 취미를 선택해주세요.",
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                lineHeight = 19.6.sp,
                                color = Color(0xFF3A3A3A),
                            ),
                        )
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    hobbies.forEach { hobby ->
                        val isSelected = hobby in selectedHobbies
                        HobbyChip(
                            label = hobby,
                            isSelected = isSelected,
                            onClick = {
                                if (isSelected) selectedHobbies.remove(hobby)
                                else selectedHobbies.add(hobby)
                            },
                        )
                    }
                    HobbyChip(
                        label = "+",
                        isSelected = false,
                        onClick = { showAddDialog = true },
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(88.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.White),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Button(
                    onClick = { onNextClick(selectedHobbies.toList()) },
                    enabled = selectedHobbies.isNotEmpty(),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9447),
                        disabledContainerColor = Color(0xFFE5E5E5),
                    ),
                ) {
                    Text(
                        text = "다음",
                        style = TextStyle(
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White,
                        ),
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        HobbyAddDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newHobby ->
                if (newHobby.isNotBlank() && newHobby !in hobbies) {
                    hobbies.add(newHobby)
                    selectedHobbies.add(newHobby)
                }
                showAddDialog = false
            },
        )
    }
}

@Composable
fun HobbyChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .then(
                if (isSelected) {
                    Modifier.background(Color(0xFFFF9447))
                } else {
                    Modifier
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(18.dp))
                },
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 19.6.sp,
                color = if (isSelected) Color.White else Color(0xFF3A3A3A),
            ),
        )
    }
}

@Composable
fun HobbyAddDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val maxLength = 20
    var inputText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(top = 24.dp, bottom = 18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "취미 입력",
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 21.6.sp,
                        color = Color(0xFF1E1E1E),
                    ),
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기",
                        tint = Color(0xFF3A3A3A),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicTextField(
                        value = inputText,
                        onValueChange = { if (it.length <= maxLength) inputText = it },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 19.6.sp,
                            color = Color(0xFF3A3A3A),
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        innerTextField()
                                    }
                                    if (inputText.isNotEmpty()) {
                                        IconButton(
                                            onClick = { inputText = "" },
                                            modifier = Modifier.size(20.dp),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "지우기",
                                                tint = Color(0xFF3A3A3A),
                                                modifier = Modifier.size(20.dp),
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = Color(0xFFB5B5B5), thickness = 1.dp)
                            }
                        },
                    )
                }
                Text(
                    text = "${inputText.length}/$maxLength",
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        color = Color(0xFFB5B5B5),
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }

            Button(
                onClick = { onConfirm(inputText.trim()) },
                enabled = inputText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9447),
                    disabledContainerColor = Color(0xFFE5E5E5),
                ),
            ) {
                Text(
                    text = "완료",
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun MyHobbySelectScreenPreview() {
    ForDayTheme {
        MyHobbySelectScreen()
    }
}
