package com.forday.app.remote.model.response

import com.forday.app.data.model.TermsConsentEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class TermsConsentResponse(
    @SerializedName("status") val status: Int? = null,
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("data") val data: TermsConsentDataResponse? = null
) : RemoteMapper<TermsConsentEntity> {
    override fun toData(): TermsConsentEntity = TermsConsentEntity(
        message = data?.message ?: ""
    )
}

data class TermsConsentDataResponse(
    @SerializedName("message") val message: String? = null
)
