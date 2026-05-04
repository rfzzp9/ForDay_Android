package com.forday.app.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class RoutineDetailRequest(
    val context: String,
    val userId: String,
    val keyword: String
)
