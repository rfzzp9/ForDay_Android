package com.forday.app.core.logger.di

import com.forday.app.core.logger.crashlytics.CrashlyticsManager
import com.forday.app.core.logger.crashlytics.CrashlyticsManagerImpl
import com.forday.app.core.logger.timber.CrashlyticsTree
import com.forday.app.core.logger.timber.DebugLogTree
import com.forday.app.core.logger.timber.TimberInitializer
import com.google.firebase.crashlytics.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoggerModule {

    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
        return FirebaseCrashlytics.getInstance()
    }

    @Provides
    @Singleton
    fun provideCrashlyticsManager(
        firebaseCrashlytics: FirebaseCrashlytics
    ): CrashlyticsManager {
        return CrashlyticsManagerImpl(firebaseCrashlytics)
    }

    @Provides
    @Singleton
    fun provideCrashlyticsTree(
        crashlyticsManager: CrashlyticsManager
    ): CrashlyticsTree {
        return CrashlyticsTree(crashlyticsManager)
    }

    @Provides
    @Singleton
    fun provideTimberInitializer(
        crashlyticsTree: CrashlyticsTree
    ): TimberInitializer {
        val trees = mutableListOf<Timber.Tree>()

        // Debug 빌드에서는 DebugLogTree 추가
        if (BuildConfig.DEBUG) {
            trees.add(DebugLogTree())
        }

        // Release 빌드에서는 CrashlyticsTree만 추가
        trees.add(crashlyticsTree)

        return TimberInitializer(*trees.toTypedArray())
    }
}