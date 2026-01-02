package com.forday.app.remote.di

import com.forday.app.data.remote.RemoteDataSource
import com.forday.app.remote.impl.RemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RemoteDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindMovieRemoteDataSource(source: RemoteDataSourceImpl): RemoteDataSource

}