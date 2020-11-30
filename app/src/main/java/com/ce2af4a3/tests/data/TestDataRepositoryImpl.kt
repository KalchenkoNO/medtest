package com.ce2af4a3.tests.data

import com.ce2af4a3.tests.domain.TestData
import com.ce2af4a3.tests.domain.TestDataRepository
import com.ce2af4a3.tests.utils.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TestDataRepositoryImpl @Inject constructor(
    private val parser: Parser<String, TestData>,
    private val api: TestsApi
): TestDataRepository {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getTestData(urn: String): TestData = withContext(Dispatchers.IO){
        parser(api.getTestData(urn).string())
    }
}