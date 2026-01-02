package com.forday.app.presentation.onboarding

import com.forday.app.domain.model.DomainEntity

data class ExampleModel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
)

fun DomainEntity.toPresentation() = ExampleModel(id, name, description)

