package com.forday.app.core.firebase.di

import android.content.Context
import com.dayn.forday.BuildConfig
import com.forday.app.core.firebase.ONBOARDING_VARIANT_KEY
import com.forday.app.core.firebase.ONBOARDING_VARIANT_OLD
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteConfigModule {

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(
        @ApplicationContext context: Context
    ): FirebaseRemoteConfig {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        return FirebaseRemoteConfig.getInstance().apply {
            setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(
                        if (BuildConfig.DEBUG) 0 else ONBOARDING_FETCH_INTERVAL_SECONDS
                    )
                    .build()
            )
            setDefaultsAsync(
                mapOf(
                    ONBOARDING_VARIANT_KEY to ONBOARDING_VARIANT_OLD,
                )
            )
        }
    }

    private const val ONBOARDING_FETCH_INTERVAL_SECONDS = 12 * 60 * 60L
}
