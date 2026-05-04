package com.forday.app.remote.impl

import com.forday.app.data.model.CancelScrapEntity
import com.forday.app.data.model.ReportPostingEntity
import com.forday.app.data.model.DeletePostingEntity
import com.forday.app.data.model.ModifyPostingEntity
import com.forday.app.data.model.ReactionCancelEntity
import com.forday.app.data.model.ReactionEntity
import com.forday.app.data.model.HobbyChipsEntity
import com.forday.app.data.model.ReactionSummaryFirstEntity
import com.forday.app.data.model.ReactionSummaryMoreEntity
import com.forday.app.data.model.ReactionUsersEntity
import com.forday.app.data.model.RoutineRecordDetailEntity
import com.forday.app.data.model.ScrapEntity
import com.forday.app.data.model.ScrapListEntity
import com.forday.app.data.model.UserFeedEntity
import com.forday.app.data.model.VisibilityEntity
import com.forday.app.data.remote.RoutineDataSource
import com.forday.app.remote.api.service.RoutineApi
import com.forday.app.remote.model.request.GetUserScrapListRequest
import com.forday.app.remote.model.request.ModifyPostingRequest
import com.forday.app.remote.model.request.PostingVisibilityRequest
import com.forday.app.remote.model.request.ReactionRequest
import com.forday.app.remote.model.request.ReportPostingRequest
import com.forday.app.remote.model.response.toData
import javax.inject.Inject

class RoutineDataSourceImpl @Inject constructor(
    private val routineApi: RoutineApi
) : RoutineDataSource {
    override suspend fun getMyRoutineRecordDetail(recordId: Int): RoutineRecordDetailEntity? =
        routineApi.getMyRoutineRecordDetail(recordId).toData()

    override suspend fun getMyRoutineRecordDetailWithSwipe(
        recordId: Int,
        context: String,
        userId: String?,
        keyword: String?,
        hobbyIds: List<Long>,
        notificationId: Long?
    ): RoutineRecordDetailEntity? =
        routineApi.getMyRoutineRecordDetailWithSwipe(
            recordId = recordId,
            context = context,
            userId = userId,
            keyword = keyword,
            hobbyIds = hobbyIds,
            notificationId = notificationId
        ).toData()

    override suspend fun reactionToRoutinePosting(
        recordId: Int,
        reactionType: String
    ): ReactionEntity =
        routineApi.reactionToRoutinePosting(recordId, ReactionRequest(reactionType)).toData()

    override suspend fun cancelMyReaction(
        recordId: Int,
        reactionType: String
    ): ReactionCancelEntity =
        routineApi.cancelMyReaction(recordId, reactionType).toData()

    override suspend fun modifyPostingVisibility(
        recordId: Int,
        visibility: String
    ): VisibilityEntity =
        routineApi.modifyPostingVisibility(recordId, PostingVisibilityRequest(visibility)).toData()

    override suspend fun getReactionUsers(
        recordId: Int,
        reactionType: String,
        lastUserId: String,
        size: Int
    ): ReactionUsersEntity =
        routineApi.getReactionUsers(recordId, reactionType, lastUserId, size).toData()

    override suspend fun getUserFeedList(
        hobbyIds: List<Int?>,
        lastRecordId: Long?,
        feedSize: Long?,
        userId: String?
    ): UserFeedEntity =
        routineApi.getMyRoutineFeedList(hobbyIds, lastRecordId, feedSize, userId).toData()

    override suspend fun modifyPosting(
        recordId: Int,
        modifyPostingRequest: ModifyPostingRequest
    ): ModifyPostingEntity =
        routineApi.modifyPosting(recordId, modifyPostingRequest).toData()

    override suspend fun deletePosting(recordId: Long): DeletePostingEntity =
        routineApi.deletePosting(recordId).toData()

    override suspend fun getUserScrapList(
        lastScrapId: Long?,
        size: Long?,
        userId: String?
    ): ScrapListEntity =
        routineApi.getUserScrapList(lastScrapId, size, userId).toData()

    override suspend fun scrapPosting(recordId: Int): ScrapEntity =
        routineApi.scrapPosting(recordId).toData()

    override suspend fun cancelScrapPosting(recordId: Int): CancelScrapEntity =
        routineApi.cancelScrap(recordId).toData()

    override suspend fun reportPosting(recordId: Int, reason: String): ReportPostingEntity =
        routineApi.reportPosting(recordId, ReportPostingRequest(reason)).toData()

    override suspend fun getReactionUsersFirst(recordId: Int, size: Int): ReactionSummaryFirstEntity? =
        routineApi.getReactionUsersFirst(recordId, size).toData()

    override suspend fun getReactionUsersMore(recordId: Int, type: String?, lastReactionId: Long, size: Int): ReactionSummaryMoreEntity? =
        routineApi.getReactionUsersMore(recordId, type, lastReactionId, size).toData()

    override suspend fun getHobbyChips(status: String): HobbyChipsEntity =
        routineApi.getHobbyChips(status).toData()
}