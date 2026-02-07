package com.forday.app.data.remote

import com.forday.app.data.model.IsNicknameDuplicateEntity
import com.forday.app.data.model.ProfileEntity
import com.forday.app.data.model.ProfileImageEntity
import com.forday.app.data.model.RegisterNicknameEntity
import com.forday.app.data.model.UserDataEntity

interface UserDataSource {
    suspend fun checkNicknameDuplicate(nickname: String): IsNicknameDuplicateEntity
    suspend fun registerNickname(nickname: String?): RegisterNicknameEntity
    suspend fun getUserInfo(): ProfileEntity
    suspend fun setProfileImage(imageUrl: String): ProfileImageEntity
}