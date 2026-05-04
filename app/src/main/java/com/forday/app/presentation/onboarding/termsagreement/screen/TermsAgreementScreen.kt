package com.forday.app.presentation.onboarding.termsagreement.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dayn.forday.R
import com.forday.app.presentation.onboarding.OnboardingViewModel

// ─── Design Tokens ───────────────────────────────────────────────────────────

private val ColorPrimary       = Color(0xFFFF9447)
private val ColorBgHighlight   = Color(0xFFFFF5EE)
private val ColorNeutral900    = Color(0xFF1E1E1E)
private val ColorNeutral600    = Color(0xFF7A7A7A)
private val ColorNeutral500    = Color(0xFF9E9E9E)
private val ColorNeutral50     = Color(0xFFF9F9F9)
private val ColorStroke002     = Color(0xFFD1D1D1)
private val ColorButtonDisabled = Color(0xFFE5E5E5)

// ─── Data Model ──────────────────────────────────────────────────────────────

data class TermItem(
    val id: String,
    val label: String,
    val subLabel: String? = null,
    val isRequired: Boolean = true,
    val isLinkText: Boolean = false,
    val description: String? = null
)

private val termItems = listOf(
    TermItem(
        id = "service",
        label = "서비스 이용약관",
        subLabel = " 동의 (필수)",
        isRequired = true,
        isLinkText = true
    ),
    TermItem(
        id = "age",
        label = "만 14세 이상 확인 (필수)",
        isRequired = true
    ),
    TermItem(
        id = "privacy",
        label = "개인정보 수집 및 이용",
        subLabel = " 동의 (필수)",
        isRequired = true,
        isLinkText = true
    ),
    TermItem(
        id = "notification",
        label = "게시글 좋아요 알림 수신 동의 (선택)",
        isRequired = false,
        description = "내 게시글에 감정 기록이 달리면 알려드려요."
    )
)

// ─── Screen Root ─────────────────────────────────────────────────────────────

@Composable
fun TermsAgreementRoute(
    onBackClick: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TermsAgreementScreen(
        isKakaoLogin = uiState.socialType == "KAKAO",
        onBackClick = onBackClick,
        onNextClick = { serviceConsent, ageOver14Consent, privateConsent, recordPushConsent ->
            viewModel.consentTerms(serviceConsent, ageOver14Consent, privateConsent, recordPushConsent)
        }
    )
}

// ─── Screen ──────────────────────────────────────────────────────────────────

@Composable
fun TermsAgreementScreen(
    isKakaoLogin: Boolean = true,
    onBackClick: () -> Unit = {},
    onNextClick: (serviceConsent: Boolean, ageOver14Consent: Boolean, privateConsent: Boolean, recordPushConsent: Boolean) -> Unit = { _, _, _, _ -> }
) {
    val visibleTermItems = remember(isKakaoLogin) {
        if (isKakaoLogin) termItems else termItems.filter { it.id != "notification" }
    }

    val checkedState = remember {
        mutableStateMapOf<String, Boolean>().apply {
            termItems.forEach { put(it.id, false) }
        }
    }

    val allChecked = visibleTermItems.all { checkedState[it.id] == true }
    val requiredAllChecked = visibleTermItems.filter { it.isRequired }.all { checkedState[it.id] == true }

    val buttonColor by animateColorAsState(
        targetValue = if (requiredAllChecked) ColorPrimary else ColorButtonDisabled,
        animationSpec = tween(durationMillis = 250),
        label = "buttonColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorNeutral50)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ── Top Bar ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로 가기",
                        tint = ColorNeutral900
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Title ────────────────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "포데이 서비스 이용을 위해\n약관 동의가 필요해요.",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        color = ColorNeutral900
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── 전체 동의 Card ────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ColorBgHighlight)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val newValue = !allChecked
                            visibleTermItems.forEach { checkedState[it.id] = newValue }
                        }
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CheckboxSquare(checked = allChecked)
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = ColorNeutral900, fontWeight = FontWeight.Medium)) {
                                    append("전체 동의")
                                }
                                withStyle(SpanStyle(color = ColorNeutral600, fontWeight = FontWeight.Medium)) {
                                    append(" (선택항목 포함)")
                                }
                            },
                            fontSize = 14.sp,
                            lineHeight = 19.6.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Individual Terms ──────────────────────────────────────────
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    visibleTermItems.forEach { term ->
                        TermRow(
                            term = term,
                            checked = checkedState[term.id] == true,
                            onCheckedChange = { checkedState[term.id] = it }
                        )
                    }
                }
            }
        }

        // ── Bottom Button ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.38f to Color.White
                    )
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    if (requiredAllChecked) onNextClick(
                        checkedState["service"] == true,
                        checkedState["age"] == true,
                        checkedState["privacy"] == true,
                        if (isKakaoLogin) checkedState["notification"] == true else false
                    )
                },
                enabled = requiredAllChecked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    disabledContainerColor = buttonColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .width(328.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "다음",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 19.2.sp,
                    color = Color.White
                )
            }
        }
    }
}

// ─── Term Row ─────────────────────────────────────────────────────────────────

@Composable
private fun TermRow(
    term: TermItem,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) },
        verticalAlignment = if (term.description != null) Alignment.Top else Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CheckboxSquare(checked = checked)

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (term.isLinkText && term.subLabel != null) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = ColorNeutral900,
                                fontWeight = FontWeight.Medium,
                                textDecoration = TextDecoration.Underline
                            )
                        ) { append(term.label) }
                        withStyle(
                            SpanStyle(color = ColorNeutral900, fontWeight = FontWeight.Medium)
                        ) { append(term.subLabel.substringBefore("(")) }
                        withStyle(SpanStyle(color = ColorNeutral600, fontWeight = FontWeight.Medium)) {
                            append("(" + term.subLabel.substringAfter("("))
                        }
                    },
                    fontSize = 14.sp,
                    lineHeight = 19.6.sp
                )
            } else {
                Text(
                    buildAnnotatedString {
                        val mainText = term.label.substringBefore(" (")
                        val tagText = term.label.substringAfter(" (", "")
                        withStyle(SpanStyle(color = ColorNeutral900, fontWeight = FontWeight.Medium)) {
                            append(mainText)
                        }
                        if (tagText.isNotEmpty()) {
                            withStyle(SpanStyle(color = ColorNeutral600, fontWeight = FontWeight.Medium)) {
                                append(" ($tagText")
                            }
                        }
                    },
                    fontSize = 14.sp,
                    lineHeight = 19.6.sp
                )
            }

            term.description?.let {
                Text(
                    text = it,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = ColorNeutral500,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

// ─── Custom Checkbox ──────────────────────────────────────────────────────────

@Composable
private fun CheckboxSquare(
    checked: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (checked) ColorPrimary else Color.White,
        animationSpec = tween(200),
        label = "checkboxBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) ColorPrimary else ColorStroke002,
        animationSpec = tween(200),
        label = "checkboxBorder"
    )

    Box(
        modifier = modifier
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(
                width = if (checked) 0.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun TermsAgreementScreenPreview() {
    MaterialTheme {
        TermsAgreementScreen()
    }
}
