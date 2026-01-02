package com.forday.app.remote.impl.fake

import com.forday.app.data.model.DataLayerEntity
import com.forday.app.data.remote.RemoteDataSource
import com.forday.app.remote.model.response.ExampleReponse

class FakeRemoteDataSource : RemoteDataSource {

    // 테스트 제어용
    var shouldReturnError = false
    var responseData: DataLayerEntity = getDefaultUsers()

    override suspend fun getExampleData(): DataLayerEntity {
        if (shouldReturnError) throw Exception("Test error")
        return responseData
    }

    private fun getDefaultUsers() =
            ExampleReponse(id = "1231", name = "Bella", description = "Android").toData()
}