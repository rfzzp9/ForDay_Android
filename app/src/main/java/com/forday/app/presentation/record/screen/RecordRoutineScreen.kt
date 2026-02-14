package com.forday.app.presentation.record.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.button.AddHobbyButton
import com.forday.app.core.designsystem.component.button.BottomButtonState
import com.forday.app.core.designsystem.component.button.BottomNextButton
import com.forday.app.core.designsystem.component.dropdown.DropdownItem
import com.forday.app.core.designsystem.component.dropdown.RoutineDropdown
import com.forday.app.core.designsystem.component.dropdown.VisibilityOption
import com.forday.app.core.designsystem.component.dropdown.VisibilitySelector
import com.forday.app.presentation.mypage.routinedetail.RoutineRecordDetailUiModel
import com.forday.app.presentation.record.RecordRoutineViewModel
import com.forday.app.presentation.record.RoutineUiModel
import timber.log.Timber
import java.io.File

data class StickerItem(
    val id: Int,
    val iconRes: Int,
    val isSelected: Boolean = false
)

@Composable
fun RecordRoutineScreenRoot(
    hobbyId: Long?,
    onComplete: (Long) -> Unit,
    modifyData: RoutineRecordDetailUiModel?,
    modifyMode: Boolean,
    viewModel: RecordRoutineViewModel,
    onClose: () -> Unit,
    onRoutineCreate: () -> Unit = {},
    onAddHobbyClick: () -> Unit = {}
) {
    viewModel.logEvent("record_routine_screen")
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Timber.e("@@@@@@@@########## "+modifyData?.recordId)
    Timber.e("@@@@@@@@########## hobbyId : "+hobbyId)
    fun getStickerFileName(iconRes: Int): String {
        return when (iconRes) {
            R.drawable.ic_sticker_smile -> "smile.jpg"
            R.drawable.ic_sticker_sad -> "sad.jpg"
            R.drawable.ic_sticker_laugh -> "laugh.jpg"
            R.drawable.ic_sticker_angry -> "angry.jpg"
            else -> ""
        }
    }

    LaunchedEffect(Unit) {
        state.routines.size
        val effectiveHobbyId = hobbyId ?: modifyData?.hobbyId?.toLong()
        Timber.d("fetchSpecificRoutineList - hobbyId: $hobbyId, modifyData.hobbyId: ${modifyData?.hobbyId}, effectiveHobbyId: $effectiveHobbyId")
        if (effectiveHobbyId != null) {
            viewModel.fetchSpecificRoutineList(effectiveHobbyId, null)
        }

    }

    var stickers by remember {
        mutableStateOf(
            listOf(
                StickerItem(1, R.drawable.ic_sticker_smile, isSelected = false),
                StickerItem(2, R.drawable.ic_sticker_sad, isSelected = false),
                StickerItem(3, R.drawable.ic_sticker_laugh, isSelected = false),
                StickerItem(4, R.drawable.ic_sticker_angry, isSelected = false)
            )
        )
    }

    var memoText by remember { mutableStateOf("") }
    var selectedRoutineIndex by remember { mutableStateOf<Int?>(null) }
    var showDropdown by remember { mutableStateOf(false) }
    var showPrivacyDropdown by remember { mutableStateOf(false) }
    var selectedVisibility by remember { mutableStateOf(VisibilityOption.PUBLIC) }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadComplete by remember { mutableStateOf(false) }

    // ✅ 수정모드: 기존 이미지 관리
    var existingImageUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var removedExistingImageUrls by remember { mutableStateOf<List<String>>(emptyList()) }

    var shouldOpenGallery by remember { mutableStateOf(false) }

    // ✅ 수정모드 초기화: memo, visibility, sticker, existingImageUrls
    LaunchedEffect(modifyMode, modifyData) {
        if (modifyMode && modifyData != null) {
            viewModel.getMyRoutineRecordDetail(modifyData.recordId)
            // memo
            memoText = modifyData.memo

            // visibility
            selectedVisibility = if (modifyData.isPublic) VisibilityOption.PUBLIC else VisibilityOption.PRIVATE

            // sticker - stickerUrl 키워드로 매칭
            val stickerIconRes = when {
                modifyData.stickerUrl.contains("smile", ignoreCase = true) -> R.drawable.ic_sticker_smile
                modifyData.stickerUrl.contains("sad", ignoreCase = true)   -> R.drawable.ic_sticker_sad
                modifyData.stickerUrl.contains("laugh", ignoreCase = true) -> R.drawable.ic_sticker_laugh
                modifyData.stickerUrl.contains("angry", ignoreCase = true) -> R.drawable.ic_sticker_angry
                else -> null
            }
            if (stickerIconRes != null) {
                stickers = stickers.map { it.copy(isSelected = it.iconRes == stickerIconRes) }
            }

            // 기존 이미지 URL 파싱 (comma separated)
            if (modifyData.imageUrl.isNotEmpty()) {
                existingImageUrls = modifyData.imageUrl.split(", ").map { it.trim() }.filter { it.isNotEmpty() }
            }
        }
    }

    // ✅ 남은 기존 이미지 + 전체 이미지 수 계산
    val activeExistingImageUrls = existingImageUrls.filter { it !in removedExistingImageUrls }
    val totalImageCount = activeExistingImageUrls.size + selectedImages.size

    fun startImageUpload(newUris: List<Uri>) {
        if (newUris.isEmpty()) return

        val remainingSlots = 1 - totalImageCount
        val imagesToAdd = newUris.take(remainingSlots)
        selectedImages = selectedImages + imagesToAdd

        Timber.d("Selected ${imagesToAdd.size} images, total: ${selectedImages.size}")
        Timber.d("Starting immediate upload for selected images")

        isUploading = true
        uploadComplete = false

        val imageInfoList = selectedImages.mapIndexed { index, uri ->
            mapOf(
                "fileName" to getFileName(context, uri),
                "contentType" to getContentType(context, uri),
                "usage" to "ACTIVITY_RECORD",
                "order" to (index + 1)
            )
        }

        Timber.d("Requesting presigned URLs for ${imageInfoList.size} images immediately after selection")
        viewModel.getPresignedUrl(imageInfoList)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        Timber.d("Photo Picker callback - selected: $uri")
        if (uri != null) {
            startImageUpload(listOf(uri))
        }
        shouldOpenGallery = false
    }

    val legacyGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { intent ->
            val uri = intent.data
            if (uri != null) {
                Timber.d("Legacy Gallery callback - selected: $uri")
                startImageUpload(listOf(uri))
            }
        }
        shouldOpenGallery = false
    }

    // ✅ 3. 권한 승인 후 갤러리 열기
    LaunchedEffect(shouldOpenGallery) {
        if (shouldOpenGallery) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13 이상: Photo Picker 사용
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                // Android 12 이하: 기존 갤러리 Intent 사용
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                    type = "image/*"
                }
                legacyGalleryLauncher.launch(intent)
            }
        }
    }

    // ✅ routineList 로드 후 index 매칭 (수정모드/신규모드 분기)
    LaunchedEffect(state.recordDetail.routineList) {
        if (state.recordDetail.routineList.isNotEmpty()) {
            if (modifyMode && modifyData != null) {
                var idx = state.recordDetail.routineList.indexOfFirst { it.routineId == modifyData.routineId }
                if (idx == -1) {
                    idx = state.recordDetail.routineList.indexOfFirst { it.content == modifyData.content }
                }
                Timber.d("modifyData.routineId=${modifyData.routineId}, matched idx=$idx, list=${state.recordDetail.routineList.map { "${it.routineId}:${it.content}" }}")
                selectedRoutineIndex = if (idx != -1) idx else 0
            } else {
                if (selectedRoutineIndex == null) selectedRoutineIndex = 0
            }
        }
    }

    LaunchedEffect(state.imageUploadState) {
        state.imageUploadState?.let { uploadState ->
            if (uploadState.images.isNotEmpty() && isUploading) {
                Timber.d("Presigned URLs received: ${uploadState.images.size}")

                uploadState.images.forEachIndexed { index, presignedItem ->
                    if (!presignedItem.isUploading && !presignedItem.isSuccess) {
                        val uri = selectedImages.getOrNull(index)
                        uri?.let {
                            Timber.d("Starting upload for image $index: ${presignedItem.uploadUrl}")
                            val file = uriToFile(context, uri)

                            if (file != null) {
                                viewModel.uploadImageToS3(
                                    file = file,
                                    uploadUrl = presignedItem.uploadUrl,
                                    contentType = getContentType(context, uri),
                                    order = presignedItem.order
                                )
                            } else {
                                Timber.e("Failed to convert URI to File: $uri")
                            }
                        }
                    }
                }

                if (uploadState.images.all { it.isSuccess }) {
                    Timber.d("All images uploaded successfully!")
                    isUploading = false
                    uploadComplete = true
                }
            }
        }
    }

    // ✅ 수정모드: modifyPosting 결과 관찰 → 성공 시 onComplete 호출
    LaunchedEffect(state.modifyPostingUiModel) {
        val result = state.modifyPostingUiModel ?: return@LaunchedEffect

        val recordIdForNavigation =
            state.recordDetail.routineRecordId.takeIf { it > 0 }
                ?: modifyData?.recordId
                ?: result.activityId

        onComplete(recordIdForNavigation.toLong())
        Timber.e("@@@@@@@@########## "+recordIdForNavigation.toLong())
        viewModel.resetModifyPostingUiModel()
    }

    RecordRoutineScreen(
        routineList = state.recordDetail.routineList,
        selectedRoutineIndex = selectedRoutineIndex,
        showDropdown = showDropdown,
        onActivityClick = { showDropdown = !showDropdown },
        onRoutineSelected = { index ->
            selectedRoutineIndex = index
            showDropdown = false
        },
        stickers = stickers,
        onStickerClick = { stickerId ->
            stickers = stickers.map {
                it.copy(isSelected = it.id == stickerId)
            }
        },
        onClose = onClose,
        memoText = memoText,
        onMemoChange = { memoText = it },
        selectedVisibility = selectedVisibility,
        showPrivacyDropdown = showPrivacyDropdown,
        onPrivacyClick = { showPrivacyDropdown = !showPrivacyDropdown },
        onVisibilitySelected = { option ->
            selectedVisibility = option
            showPrivacyDropdown = false
        },
        selectedImages = selectedImages,
        existingImageUrls = activeExistingImageUrls,
        onExistingImageRemove = { url ->
            removedExistingImageUrls = removedExistingImageUrls + url
        },
        onPhotoClick = {
            if (totalImageCount < 1) {
                Timber.d("Photo button clicked - opening gallery")
                shouldOpenGallery = true
            }
        },
        onImageRemove = { uri ->
            val removeIndex = selectedImages.indexOf(uri)

            if (removeIndex != -1) {
                state.imageUploadState?.images?.getOrNull(removeIndex)?.let { uploadedImage ->
                    if (uploadedImage.isSuccess && uploadedImage.fileUrl.isNotEmpty()) {
                        Timber.d("Deleting S3 image at index $removeIndex: ${uploadedImage.fileUrl}")
                        viewModel.deleteS3Image(uploadedImage.fileUrl)
                    }
                }

                selectedImages = selectedImages.filter { it != uri }

                if (selectedImages.isEmpty()) {
                    uploadComplete = false
                    isUploading = false
                }

                Timber.d("Image removed. Remaining images: ${selectedImages.size}")
            }
        },
        isUploading = isUploading,
        modifyData = modifyData,
        uploadComplete = uploadComplete,
        modifyMode = modifyMode,
        onRoutineCreate = onRoutineCreate,
        hobbyId = hobbyId,
        onAddHobbyClick = onAddHobbyClick,
        onComplete = {
            Timber.d("Complete button clicked, modifyMode: $modifyMode")

            if (selectedImages.isNotEmpty() && !uploadComplete) {
                if (isUploading) {
                    Timber.d("Upload still in progress, please wait")
                } else {
                    Timber.e("Upload not started or failed")
                }
                return@RecordRoutineScreen
            }

            val routineId = selectedRoutineIndex?.let { index ->
                state.recordDetail.routineList.getOrNull(index)?.routineId?.toLong()
            } ?: if (modifyMode && modifyData != null) {
                modifyData.routineId.toLong()
            } else {
                return@RecordRoutineScreen
            }

            val selectedSticker = stickers.find { it.isSelected }
            val stickerFileName = selectedSticker?.let { getStickerFileName(it.iconRes) } ?: ""
            val visibilityValue = selectedVisibility.name

            val imageUrls = if (modifyMode) {
                val newUploadedUrls = if (selectedImages.isNotEmpty()) {
                    state.imageUploadState?.images?.map { it.fileUrl } ?: emptyList()
                } else emptyList()
                (activeExistingImageUrls + newUploadedUrls).joinToString(", ")
            } else {
                if (selectedImages.isNotEmpty()) {
                    state.imageUploadState?.images?.joinToString(", ") { it.fileUrl } ?: ""
                } else ""
            }

            if (modifyMode && modifyData != null) {
                Timber.d("Calling modifyPosting - recordId: ${modifyData.recordId}, routineId: $routineId, sticker: $stickerFileName, memo: $memoText, imageUrl: $imageUrls, visibility: $visibilityValue")
                val recordIdForRequest =
                    state.recordDetail.routineRecordId.takeIf { it > 0 } ?: modifyData.recordId

                viewModel.modifyPosting(
                    recordId = recordIdForRequest,
                    routineId = routineId.toInt(),
                    sticker = stickerFileName,
                    memo = memoText,
                    imageUrl = imageUrls,
                    visibility = visibilityValue
                )
            } else {
                Timber.d("Calling writeRoutine - routineId: $routineId, sticker: $stickerFileName, memo: $memoText, imageUrl: $imageUrls, visibility: $visibilityValue")

                viewModel.writeRoutine(
                    routineId = routineId,
                    sticker = stickerFileName,
                    memo = memoText,
                    imageUrl = imageUrls,
                    visibility = visibilityValue,
                    onSuccess = { newRecordId ->
                        onComplete(newRecordId)
                    }
                )
            }
        }
    )
}

@Composable
fun RecordRoutineScreen(
    routineList: List<RoutineUiModel> = emptyList(),
    selectedRoutineIndex: Int? = null,
    showDropdown: Boolean = false,
    onActivityClick: () -> Unit = {},
    onRoutineSelected: (Int) -> Unit = {},
    stickers: List<StickerItem> = emptyList(),
    onStickerClick: (Int) -> Unit = {},
    memoText: String = "",
    onMemoChange: (String) -> Unit = {},
    selectedVisibility: VisibilityOption = VisibilityOption.PUBLIC,
    showPrivacyDropdown: Boolean = false,
    onPrivacyClick: () -> Unit = {},
    onVisibilitySelected: (VisibilityOption) -> Unit = {},
    selectedImages: List<Uri> = emptyList(),
    existingImageUrls: List<String> = emptyList(),
    onExistingImageRemove: (String) -> Unit = {},
    onPhotoClick: () -> Unit = {},
    onImageRemove: (Uri) -> Unit = {},
    isUploading: Boolean = false,
    uploadComplete: Boolean = false,
    modifyMode: Boolean = false,
    onComplete: () -> Unit,
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier,
    modifyData: RoutineRecordDetailUiModel?,
    hobbyId: Long? = null,
    onAddHobbyClick: () -> Unit = {},
    onRoutineCreate: () -> Unit = {}
) {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val density = LocalDensity.current
    var activitySelectorBottomY by remember { mutableFloatStateOf(0f) }
    var containerTopY by remember { mutableFloatStateOf(0f) }

    val selectedActivity = if (modifyMode && modifyData != null && selectedRoutineIndex == null) {
        modifyData.content
    } else {
        selectedRoutineIndex?.let {
            routineList.getOrNull(it)?.content
        } ?: "활동을 선택해주세요"
    }

    Scaffold(
        topBar = {
            RecordActivityTopBar(
                onClose = onClose,
                modifyMode = modifyMode
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .onGloballyPositioned { containerTopY = it.positionInRoot().y }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    softwareKeyboardController?.hide()
                }
        ) {
            if (isUploading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = false) { },
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
                            text = "이미지 업로드 중...\n메모를 작성하시면 됩니다",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(top = 7.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "활동 (필수)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3A3A3A)
                    )

                    if (hobbyId == null) {
                        AddHobbyButton(
                            text = "취미 추가하기",
                            onClick = onAddHobbyClick
                        )
                    } else if (routineList.isEmpty()) {
                        AddHobbyButton(
                            text = "취미활동 추가하기",
                            onClick = onRoutineCreate
                        )
                    } else {
                        Box(
                            modifier = Modifier.onGloballyPositioned { coords ->
                                activitySelectorBottomY = coords.positionInRoot().y + coords.size.height
                            }
                        ) {
                            ActivitySelector(
                                selectedActivity = selectedActivity,
                                onClick = onActivityClick
                            )
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "스티커 선택 (필수)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3A3A3A)
                    )

                    StickerGrid(
                        stickers = stickers,
                        onStickerClick = onStickerClick
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "한 줄 메모 (선택)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3A3A3A)
                    )

                    MemoInputField(
                        text = memoText,
                        onTextChange = onMemoChange,
                        selectedImages = selectedImages,
                        existingImageUrls = existingImageUrls,
                        onExistingImageRemove = onExistingImageRemove,
                        onPhotoClick = onPhotoClick,
                        onImageRemove = onImageRemove,
                        uploadComplete = uploadComplete
                    )
                }

                PrivacySelector(
                    selectedOption = selectedVisibility.label,
                    onClick = onPrivacyClick
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            if (showDropdown) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onActivityClick
                        )
                )
            }

            if (showDropdown && routineList.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .offset(y = with(density) { (activitySelectorBottomY - containerTopY).toDp() } + 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    RoutineDropdown(
                        items = routineList.mapIndexed { index, routine ->
                            DropdownItem(
                                text = routine.content,
                                hasAiIcon = routine.isAiRecommended,
                                isSelected = index == selectedRoutineIndex
                            )
                        },
                        onItem = { index ->
                            onRoutineSelected(index)
                        },
                        modifier = Modifier.width(IntrinsicSize.Max)
                    )
                }
            }

            if (showPrivacyDropdown) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = (-88 - 56 - 16).dp)
                ) {
                    VisibilitySelector(
                        selectedOption = selectedVisibility,
                        onOptionSelected = onVisibilitySelected,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            BottomNextButton(
                text = if (modifyMode) "수정완료" else "작성완료",
                state = if ((selectedRoutineIndex != null || (modifyMode && modifyData != null))
                    && stickers.any { it.isSelected }
                    && !isUploading
                    && (selectedImages.isEmpty() || uploadComplete)) {
                    BottomButtonState.ENABLED
                } else {
                    BottomButtonState.DISABLED
                },
                onClick = onComplete,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun RecordActivityTopBar(
    onClose: () -> Unit,
    modifyMode: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(Color.White)
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 11.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "닫기",
                tint = Color(0xFF3A3A3A)
            )
        }

        Text(
            text = if (modifyMode) "활동 수정" else "내 활동 남기기",
            modifier = Modifier.align(Alignment.Center),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3A3A3A)
        )
    }
}

@Composable
private fun ActivitySelector(
    selectedActivity: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF2F2F2)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedActivity,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF3A3A3A)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF7A7A7A)
            )
        }
    }
}

@Composable
private fun StickerGrid(
    stickers: List<StickerItem>,
    onStickerClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stickers.forEach { sticker ->
            StickerItem(
                sticker = sticker,
                onClick = { onStickerClick(sticker.id) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StickerItem(
    sticker: StickerItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = if (sticker.isSelected) BorderStroke(1.dp, Color(0xFFFF9447)) else BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(sticker.iconRes),
                contentDescription = null,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}

@Composable
private fun MemoInputField(
    text: String,
    onTextChange: (String) -> Unit,
    selectedImages: List<Uri>,
    existingImageUrls: List<String> = emptyList(),
    onExistingImageRemove: (String) -> Unit = {},
    onPhotoClick: () -> Unit,
    onImageRemove: (Uri) -> Unit,
    uploadComplete: Boolean = false
) {
    val totalImageCount = existingImageUrls.size + selectedImages.size

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9F9F9)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                value = text,
                onValueChange = { if (it.length <= 100) onTextChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = {
                    Text(
                        text = "오늘의 취미활동을 적어주세요.",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    color = Color(0xFF3A3A3A)
                )
            )

            if (totalImageCount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    existingImageUrls.forEach { url ->
                        Box(
                            modifier = Modifier.size(60.dp)
                        ) {
                            AsyncImage(
                                model = url,
                                contentDescription = "기존 이미지",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Image(
                                painter = painterResource(id = R.drawable.ic_close_btn),
                                contentDescription = "삭제",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        onExistingImageRemove(url)
                                    }
                            )
                        }
                    }

                    selectedImages.forEach { uri ->
                        Box(
                            modifier = Modifier.size(60.dp)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = "선택된 이미지",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Image(
                                painter = painterResource(id = R.drawable.close_image),
                                contentDescription = "삭제",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        onImageRemove(uri)
                                    }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Surface(
                    onClick = onPhotoClick,
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = if (totalImageCount >= 1) Color(0xFFE5E5E5) else Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5E5E5))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (totalImageCount == 0) {
                            Icon(
                                painter = painterResource(R.drawable.ic_camera),
                                contentDescription = "사진 추가",
                                modifier = Modifier.padding(14.dp),
                                tint = Color(0xFF7A7A7A)
                            )
                        } else {
//                            Text(
//                                text = "$totalImageCount/1",
//                                fontSize = 12.sp,
//                                fontWeight = FontWeight.Medium,
//                                color = if (totalImageCount >= 1) Color(0xFFB5B5B5) else Color(0xFF7A7A7A)
//                            )
                        }
                    }
                }

                Text(
                    text = "${text.length}/100",
                    fontSize = 10.sp,
                    color = Color(0xFFB5B5B5),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun PrivacySelector(
    selectedOption: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "기록 공개범위",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3A3A3A)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedOption,
                fontSize = 14.sp,
                color = Color(0xFF7A7A7A)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF7A7A7A)
            )
        }
    }
}

@Composable
private fun CompleteButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifyMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0f),
                            Color.White
                        ),
                        startY = 0f,
                        endY = 88.dp.value
                    )
                )
        )

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (enabled) Color(0xFFFF9447) else Color(0xFFE5E5E5),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (modifyMode) "수정완료" else "작성완료",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun getFileName(context: Context, uri: Uri): String {
    var fileName = "image_${System.currentTimeMillis()}.jpg"

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
fun RecordActivityScreenPreview() {
    RecordRoutineScreen(
        onComplete = {},
        modifyData = null
    )
}