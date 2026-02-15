package com.forday.app.presentation.allsettings.cancelaccount.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.button.BottomButtonState
import com.forday.app.core.designsystem.component.button.BottomNextButton
import com.forday.app.core.designsystem.theme.ForDayTheme
import com.forday.app.presentation.allsettings.SettingsViewModel

@Composable
fun CancelAccountScreen(
    onBackClick: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    viewModel: SettingsViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isAgreed by remember { mutableStateOf(false) }

    // 탈퇴 성공 시 로그인 화면으로 이동
    LaunchedEffect(uiState.isAccountCancelled) {
        if (uiState.isAccountCancelled) {
            onNavigateToLogin()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header
            CancelAccountHeader(onBackClick = onBackClick)

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Icon
                Image(
                    painter = painterResource(id = R.drawable.icon_exit_app),
                    contentDescription = "탈퇴",
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Main Title
                Text(
                    text = "더 좋은 서비스를 제공하지 못하여 죄송합니다.",
                    style = TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 25.2.sp,
                        fontFamily = FontFamily(Font(R.font.pretendard_std_variable)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFF1A1A1A)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = "탈퇴하시면 모든 활동기록과 데이터가 파기되며\n모든 데이터는 복구가 불가능합니다.",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 19.6.sp,
                        fontFamily = FontFamily(Font(R.font.pretendard_std_variable)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF3A3A3A)
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 회원정보 Section
                InfoSection(
                    title = "회원정보",
                    description = "소셜 계정 로그인아이디 정보가 삭제됩니다."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 활동기록 정보 Section
                InfoSection(
                    title = "활동기록 정보",
                    description = "수집된 취미명, 목적, 취미빈도, 취미시간, AI 추천활동, 활동 등 모든 취미정보와 활동기록과 관련된 정보들이 삭제됩니다."
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(
                            id = if (isAgreed) R.drawable.toggle_selected else R.drawable.toggle_unselected
                        ),
                        contentDescription = if (isAgreed) "동의함" else "동의 안 함",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                onClick = rememberThrottledClick { isAgreed = !isAgreed },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "모든 정보 및 활동기록 데이터 삭제에 동의합니다.",
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 19.6.sp,
                            fontFamily = FontFamily(Font(R.font.pretendard_std_variable)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF7A7A7A)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Bottom Button
        BottomNextButton(
            text = "탈퇴하기",
            state = if (isAgreed) BottomButtonState.ENABLED else BottomButtonState.DISABLED,
            onClick = { viewModel.cancelAccount() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun CancelAccountHeader(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_chevron_left),
            contentDescription = "뒤로가기",
            tint = Color.Black,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .clickable(
                    onClick = onBackClick,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        )

        Text(
            text = "탈퇴하기",
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.pretendard_std_variable)),
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun InfoSection(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF9F9F9),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 19.6.sp,
                fontFamily = FontFamily(Font(R.font.pretendard_std_variable)),
                fontWeight = FontWeight(500),
                color = Color(0xFF1A1A1A)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 16.8.sp,
                fontFamily = FontFamily(Font(R.font.pretendard_std_variable)),
                fontWeight = FontWeight(400),
                color = Color(0xFF9E9E9E)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CancelAccountScreenPreview() {
    ForDayTheme {
//        CancelAccountScreen()
    }
}