package com.forday.app.presentation.mypage

import com.forday.app.domain.model.RoutineReactionDomain
import com.forday.app.domain.model.RoutineRecordDetailDomain
import com.forday.app.domain.model.RoutineUserReactionDomain
import com.forday.app.presentation.mypage.routinedetail.RoutineReactionUiModel
import com.forday.app.presentation.mypage.routinedetail.RoutineRecordDetailUiModel
import com.forday.app.presentation.mypage.routinedetail.RoutineUserReactionUiModel

fun RoutineRecordDetailDomain.toPresentation(): RoutineRecordDetailUiModel {
    return RoutineRecordDetailUiModel(
        hobbyId = hobbyId,
        routineId = routineId,
        recordId = recordId,
        content = content,
        imageUrl = image,
        stickerUrl = sticker,
        isScraped = isScraped,
        writerNickname = writerNickname,
        date = date, // 여기서 날짜 포맷 가공 로직을 넣을 수 있습니다.
        memo = memo,
        isMine = isMine,
        isPublic = isVisible,
        reactions = newReaction.toPresentation(),
        myReactions = userReaction.toPresentation()
    )
}

fun RoutineReactionDomain.toPresentation(): RoutineReactionUiModel {
    return RoutineReactionUiModel(awesome, great, amazing, fighting)
}

fun RoutineUserReactionDomain.toPresentation(): RoutineUserReactionUiModel {
    return RoutineUserReactionUiModel(pressedAwesome, pressedGreat, pressedAmazing, pressedFighting)
}