package com.forday.app.remote.model.request

import com.google.gson.annotations.SerializedName

data class NotificationToggleRequest(
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("toggleType")
    val toggleType: String
)
