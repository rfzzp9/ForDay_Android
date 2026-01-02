package com.forday.app.presentation.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LoginScreen(
    onKakaoLoginClick: () -> Unit,
    onGuestModeClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(160.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 17.dp)
        ) {
            // 로고 영역
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(Color(0xFFE0E0E0))
            )

            // 타이틀
            Text(
                text = "ForDay에",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "오신 것을 환영합니다!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(15.dp))

            // 서브타이틀
            Text(
                text = "당신만의 취미 루틴, AI가 추천해드립니다",
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 카카오톡으로 시작하기 버튼
        Button(
            onClick = onKakaoLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFEE500)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_kakao), // 카카오 아이콘
//                contentDescription = "카카오",
//                modifier = Modifier.size(20.dp),
//                tint = Color.Black
//            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "카카오톡으로 시작하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 게스트로 둘러보기 버튼
        OutlinedButton(
            onClick = onGuestModeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White
            )
        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_guest), // 게스트 아이콘
//                contentDescription = "게스트",
//                modifier = Modifier.size(20.dp),
//                tint = Color.Gray
//            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "게스트로 둘러보기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onKakaoLoginClick = {},
        onGuestModeClick = {},
    )
}