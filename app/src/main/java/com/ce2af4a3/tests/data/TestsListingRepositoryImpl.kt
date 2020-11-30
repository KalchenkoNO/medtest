package com.ce2af4a3.tests.data

import com.ce2af4a3.tests.domain.Test
import com.ce2af4a3.tests.domain.TestListingRepository
import com.ce2af4a3.tests.utils.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import javax.inject.Inject

class TestsListingRepositoryImpl @Inject constructor(
    private val api: TestsApi,
    private val parser: Parser<String, List<Test>>
) : TestListingRepository {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getTests(): List<Test> = withContext(Dispatchers.IO) {
        parser(api.getTests().string())
    }
}