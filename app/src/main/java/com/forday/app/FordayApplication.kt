package com.forday.app

import android.app.Application
import android.util.Log
import com.app.forday.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FordayApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("FordayApplication", "onCreate")
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)
    }
}