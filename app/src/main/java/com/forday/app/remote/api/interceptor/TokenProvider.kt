package com.forday.app.remote.api.interceptor

interface TokenProvider {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun setAccessToken(accessToken: String)
    fun setRefreshToken(refreshToken: String)
}