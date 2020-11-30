package com.ce2af4a3.tests.domain

interface TestListingRepository {
    suspend fun getTests(): List<Test>
}