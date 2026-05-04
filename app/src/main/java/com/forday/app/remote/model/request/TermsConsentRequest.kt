package com.forday.app.remote.model.request

import com.google.gson.annotations.SerializedName

data class TermsConsentRequest(
    @SerializedName("serviceConsent") val serviceConsent: Boolean,
    @SerializedName("ageOver14Consent") val ageOver14Consent: Boolean,
    @SerializedName("privateConsent") val privateConsent: Boolean,
    @SerializedName("recordPushConsent") val recordPushConsent: Boolean
)
