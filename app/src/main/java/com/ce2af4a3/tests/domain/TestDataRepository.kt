package com.ce2af4a3.tests.domain

interface TestDataRepository {
    suspend fun getTestData(urn: String): TestData
}