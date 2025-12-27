package com.forday.app.data.impl

import com.forday.app.data.remote.RemoteDataSource
import com.forday.app.domain.model.DomainEntity
import com.forday.app.domain.repository.Repository
import javax.inject.Inject

internal class RepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
) : Repository {
    override suspend fun getExampleData(): DomainEntity =
    remoteDataSource.getExampleData().toDomain()

}