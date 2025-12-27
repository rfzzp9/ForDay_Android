package com.forday.app.domain.repository

import com.forday.app.domain.model.DomainEntity

interface Repository {

    suspend fun getExampleData(): DomainEntity

}