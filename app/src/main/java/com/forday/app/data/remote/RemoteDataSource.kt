package com.forday.app.data.remote

import com.forday.app.data.model.DataLayerEntity

interface RemoteDataSource {

    suspend fun getExampleData(): DataLayerEntity

}