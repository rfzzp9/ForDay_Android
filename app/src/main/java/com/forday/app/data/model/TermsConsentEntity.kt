package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.TermsConsentDomain

data class TermsConsentEntity(
    val message: String
) : DataMapper<TermsConsentDomain> {
    override fun toDomain(): TermsConsentDomain = TermsConsentDomain(message = message)
}
