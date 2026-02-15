package com.forday.app.presentation.mypage.profilesetting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import com.forday.app.core.designsystem.component.clickable.rememberThrottledClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dayn.forday.R
import com.forday.app.presentation.mypage.MyPageViewModel
import android.provider.OpenableColumns
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

// 색상 정의
object ProfileSettingColors {
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val Neutral900 = Color(0xFF1E1E1E)
    val Neutral800 = Color(0xFF3A3A3A)
    val Neutral500 = Color(0xFF9E9E9E)
    val Stroke001 = Color(0xFFE5E5E5)
    val Background002 = Color(0xFFF9F9F9)
}

@Composable
fun ProfileSettingScreenRoot(
    goBack: () -> Unit,
    viewModel: MyPageViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 상태 관리
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadComplete by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }  // ✅ 삭제 확인 다이얼로그

    // 추가: 삭제 예정 플래그
    var isMarkedForDeletion by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    var showUploadingToast by remember { mutableStateOf(false) }

    // 이미지 업로드 시작 함수
    fun startProfileImageUpload(uri: Uri) {
        selectedImageUri = uri
        isUploading = true
        uploadComplete = false
        showError = false

        val imageInfo = listOf(
            mapOf(
                "fileName" to getFileName(context, uri),
                "contentType" to getContentType(context, uri),
                "usage" to "PROFILE_IMAGE",
                "order" to 1
            )
        )

        viewModel.getPresignedUrl(imageInfo)
    }

    // Presigned URL 받은 후 S3 업로드
    LaunchedEffect(state.imageUploadState) {
        state.imageUploadState.let { uploadState ->
            Timber.d("ImageUploadState: isUploading=${uploadState.isUploading}, isSuccess=${uploadState.isSuccess}, uploadUrl=${uploadState.uploadUrl}, fileUrl=${uploadState.fileUrl}")

            if (isUploading) {
                // S3 업로드 시작
                if (!uploadState.isUploading &&
                    !uploadState.isSuccess &&
                    uploadState.uploadUrl != null) {

                    selectedImageUri?.let { uri ->
                        val file = uriToFile(context, uri)

                        if (file != null) {
                            Timber.d("Starting S3 upload for file: ${file.name}")
                            viewModel.uploadImageToS3(
                                file = file,
                                uploadUrl = uploadState.uploadUrl,
                                contentType = getContentType(context, uri),
                                order = 1
                            )
                        } else {
                            Timber.e("Failed to convert Uri to File")
                            isUploading = false
                            showError = true
                            errorMessage = "이미지 파일을 불러올 수 없습니다."
                        }
                    }
                }

                // 업로드 완료
                if (uploadState.isSuccess && uploadState.fileUrl != null) {
                    Timber.d("Upload success! fileUrl: ${uploadState.fileUrl}")
                    uploadComplete = true
                    isUploading = false

//                    viewModel.setProfileImage(uploadState.fileUrl)
                }
            }
        }
    }

    // 타임아웃 체크
    LaunchedEffect(isUploading) {
        if (isUploading) {
            kotlinx.coroutines.delay(10000)
            if (isUploading) {
                Timber.e("Upload timeout")
                isUploading = false
                showError = true
                errorMessage = "이미지 업로드 시간이 초과되었습니다."
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { startProfileImageUpload(it) }
    }

    val legacyGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { uri ->
            startProfileImageUpload(uri)
        }
    }

    // ✅ 삭제 확인 다이얼로그
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(text = "프로필 이미지 삭제") },
            text = { Text(text = "프로필 이미지를 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false

                        // ✅ 즉시 삭제하지 않고 플래그만 설정
                        isMarkedForDeletion = true
                        selectedImageUri = null
                        uploadComplete = false
                    }
                ) {
                    Text("삭제", color = Color(0xFFFF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = rememberThrottledClick { showDeleteConfirmDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    ProfileSettingScreen(
        profileImageUrl = state.userInfo?.profileImageUrl,
        nickName = state.userInfo?.nickName ?: "",
        onBackClick = {
            isMarkedForDeletion = false
            goBack()
        },
        onCompleteClick = {
            // ✅ 완료 버튼: 모든 변경사항 적용
            if (isUploading) {
                if (!showUploadingToast) {
                    showUploadingToast = true
                    coroutineScope.launch {
                        delay(2000)
                        showUploadingToast = false
                    }
                }
                return@ProfileSettingScreen
            }
            // 1. 삭제가 예정되어 있으면 실제 삭제 실행
            if (isMarkedForDeletion) {
                val imageUrlToDelete = state.userInfo?.profileImageUrl

                if (!imageUrlToDelete.isNullOrEmpty()) {
                    val actualUrl = imageUrlToDelete.replace("/temp/", "/")

                    Timber.d("🔴 Deleting profile image on complete")
                    Timber.d("   Original: $imageUrlToDelete")
                    Timber.d("   Actual:   $actualUrl")

                    // S3에서 삭제
                    viewModel.deleteS3Image(actualUrl)
                    // 서버에 빈 URL 저장
                    viewModel.setProfileImage("")
                }
            }
            // 2. 새 이미지 업로드가 있으면 서버에 저장
            else if (uploadComplete) {
                state.imageUploadState.fileUrl?.let {
                    viewModel.setProfileImage(it)
                }
            }

            // 3. 화면 닫기
            goBack()
        },
        onProfileImageClick = {
            // ✅ 새 이미지 선택 시 삭제 플래그 해제
            isMarkedForDeletion = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                    type = "image/*"
                }
                legacyGalleryLauncher.launch(intent)
            }
        },
        onDeleteImageClick = {  // ✅ 삭제 콜백 구현
            showDeleteConfirmDialog = true
        },
        onDuplicateCheckClick = {
            // 중복 확인 로직
        },
        isUploading = isUploading,
        isMarkedForDeletion = isMarkedForDeletion,
        uploadComplete = uploadComplete,
        selectedImageUri = selectedImageUri,
        showError = showError,
        errorMessage = errorMessage,
        onDismissError = { showError = false },
        showUploadingToast = showUploadingToast
    )

    if (showUploadingToast) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = Color(0xCC000000),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "이미지 업로드 중입니다…",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }
        }
    }
}


@Composable
fun ProfileSettingScreen(
    showUploadingToast: Boolean = false,
    modifier: Modifier = Modifier,
    profileImageUrl: String? = null,
    nickName: String = "",
    onNickNameChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onCompleteClick: () -> Unit = {},
    onProfileImageClick: () -> Unit = {},
    onDeleteImageClick: () -> Unit = {},  // ✅ 삭제 콜백 추가
    onDuplicateCheckClick: () -> Unit = {},
    isUploading: Boolean = false,
    uploadComplete: Boolean = false,
    selectedImageUri: Uri? = null,
    showError: Boolean = false,
    errorMessage: String = "",
    onDismissError: () -> Unit = {},
    isMarkedForDeletion: Boolean = false,  // ✅ 추가
) {
    var nickname by remember { mutableStateOf(nickName) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ProfileSettingColors.White)
        ) {
            // Header
            ProfileSettingHeader(
                onBackClick = onBackClick,
                onCompleteClick = onCompleteClick
            )

            Spacer(modifier = Modifier.height(68.dp))

            // Profile Image Section
            ProfileImageSection(
                profileImageUrl = profileImageUrl,
                selectedImageUri = selectedImageUri,
                onProfileImageClick = onProfileImageClick,
                onDeleteImageClick = onDeleteImageClick,  // ✅ 전달
                isMarkedForDeletion = isMarkedForDeletion,  // ✅ 전달
                uploadComplete = uploadComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Nickname Input Field
            NicknameInputField(
                nickname = nickname,
                onNicknameChange = {
                    nickname = it
                    onNickNameChange(it)
                },
                onDuplicateCheckClick = onDuplicateCheckClick,
                modifier = Modifier
                    .fillMaxWidth()  // ✅ 부모 너비를 채움

            )

            Spacer(modifier = Modifier.weight(1f))
        }

        // 업로드 중 로딩 오버레이
        if (isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "프로필 이미지 업로드 중...",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 에러 다이얼로그
        if (showError) {
            AlertDialog(
                onDismissRequest = onDismissError,
                title = {
                    Text(text = "업로드 실패")
                },
                text = {
                    Text(text = errorMessage)
                },
                confirmButton = {
                    TextButton(onClick = onDismissError) {
                        Text("확인")
                    }
                }
            )
        }
    }
}


@Composable
fun ProfileSettingHeader(
    onBackClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_chevron_left),
                contentDescription = "뒤로가기",
                tint = ProfileSettingColors.Neutral800
            )
        }

        // Title
        Text(
            text = "내 프로필 설정",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ProfileSettingColors.Neutral800
        )

        // Complete Button
        Text(
            text = "완료",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = ProfileSettingColors.Neutral800,
            modifier = Modifier.clickable(onClick = rememberThrottledClick { onCompleteClick() })
        )
    }
}

@Composable
fun ProfileImageSection(
    profileImageUrl: String?,
    selectedImageUri: Uri? = null,
    onProfileImageClick: () -> Unit,
    onDeleteImageClick: () -> Unit,
    uploadComplete: Boolean = false,
    isMarkedForDeletion: Boolean = false,  // ✅ 추가
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(1.dp, ProfileSettingColors.Stroke001, CircleShape)
                    .background(ProfileSettingColors.Stroke001)
                    .clickable(onClick = rememberThrottledClick { onProfileImageClick() }),
                contentAlignment = Alignment.Center
            ) {
                when {
                    // ✅ 우선순위 1: 삭제 예정이면 기본 아이콘 표시
                    isMarkedForDeletion -> {
                        Icon(
                            painter = painterResource(R.drawable.ic_profile_empty),
                            contentDescription = "프로필 이미지",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Unspecified
                        )
                    }
                    // ✅ 우선순위 2: 새로 선택한 이미지 (미리보기)
                    selectedImageUri != null -> {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "선택한 프로필 이미지",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    // ✅ 우선순위 3: 기존 프로필 이미지
                    !profileImageUrl.isNullOrEmpty() -> {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "프로필 이미지",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    // ✅ 우선순위 4: 기본 아이콘
                    else -> {
                        Icon(
                            painter = painterResource(R.drawable.ic_profile_empty),
                            contentDescription = "프로필 이미지",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Unspecified
                        )
                    }
                }

                // 업로드 완료 체크 표시
                if (uploadComplete && selectedImageUri != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(32.dp)
                            .background(Color(0xFF4CAF50), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "업로드 완료",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // ✅ X 버튼 (이미지가 있거나 삭제 예정일 때 표시)
            if ((!profileImageUrl.isNullOrEmpty() && !isMarkedForDeletion) || selectedImageUri != null) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 0.dp, y = 0.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF4444))
                        .clickable(onClick = rememberThrottledClick { onDeleteImageClick() }),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "프로필 이미지 삭제",
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                }
            }

            // 카메라 아이콘 (동일)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-8).dp, y = (-8).dp)
                    .clip(CircleShape)
                    .background(ProfileSettingColors.White)
                    .clickable(onClick = rememberThrottledClick { onProfileImageClick() }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = "프로필 사진 변경",
                    modifier = Modifier.size(16.dp),
                    tint = ProfileSettingColors.Neutral800
                )
            }
        }
    }
}

@Composable
fun NicknameInputField(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    onDuplicateCheckClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ✅ Column을 Row로 감싸서 중앙 정렬
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center  // ✅ 가로 중앙 정렬
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ProfileSettingColors.Background002)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Label
                    Text(
                        text = "닉네임",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ProfileSettingColors.Neutral500,
                        lineHeight = 16.8.sp
                    )

                    // Input Text
                    Text(
                        text = nickname.ifEmpty { "" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProfileSettingColors.Neutral900,
                        lineHeight = 19.2.sp
                    )
                }

                // Duplicate Check Button
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(y = 6.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ProfileSettingColors.Neutral900)
                        .clickable(onClick = rememberThrottledClick { onDuplicateCheckClick() })
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "중복확인",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ProfileSettingColors.White,
                        lineHeight = 16.8.sp
                    )
                }
            }
        }
    }
}

private fun getFileName(context: Context, uri: Uri): String {
    var fileName = "profile_${System.currentTimeMillis()}.jpg"

    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (displayNameIndex != -1) {
                fileName = cursor.getString(displayNameIndex)
            }
        }
    }

    return fileName
}

private fun getContentType(context: Context, uri: Uri): String {
    return context.contentResolver.getType(uri) ?: "image/jpeg"
}

private fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val contentResolver = context.contentResolver
        val fileName = getFileName(context, uri)
        val file = File(context.cacheDir, fileName)

        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file
    } catch (e: Exception) {
        Timber.e(e, "Failed to convert Uri to File")
        null
    }
}

@Preview
@Composable
fun ProfileSettingScreenPreview() {
    ProfileSettingScreen(
        nickName = "유지",
        onNickNameChange = {},
        onBackClick = {},
        onCompleteClick = {},
        onProfileImageClick = {},
        onDuplicateCheckClick = {},
        modifier = TODO(),
        profileImageUrl = TODO(),
    )
}