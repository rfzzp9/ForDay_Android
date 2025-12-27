package com.forday.app.data.di

import com.forday.app.data.impl.RepositoryImpl
import com.forday.app.domain.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepository(repository: RepositoryImpl): Repository

}