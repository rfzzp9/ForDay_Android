package com.forday.app.remote.impl

import com.forday.app.data.model.DataLayerEntity
import com.forday.app.data.remote.RemoteDataSource
import com.forday.app.remote.api.service.ExampleApi
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ExampleApi,
) : RemoteDataSource {
    override suspend fun getExampleData(): DataLayerEntity =
        apiService.getExampleData("", "").toData()

}