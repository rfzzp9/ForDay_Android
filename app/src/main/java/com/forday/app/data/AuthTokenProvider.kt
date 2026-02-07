package com.forday.app.data

import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.remote.api.interceptor.TokenProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class AuthTokenProvider @Inject constructor(
    private val userDataSource: UserLocalDataSource
) : TokenProvider {
    override fun getAccessToken(): String? = runBlocking {
        userDataSource.accessTokenFlow.firstOrNull()
    }

    override fun getRefreshToken(): String? = runBlocking {
        userDataSource.refreshTokenFlow.firstOrNull()
    }

    override fun setAccessToken(accessToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            userDataSource.saveAccessToken(accessToken)
        }
    }

    override fun setRefreshToken(refreshToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            userDataSource.saveRefreshToken(refreshToken)
        }
    }
}