package com.ce2af4a3.tests.data

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface TestsApi {
    @GET("/test.html")
    suspend fun getTests(): ResponseBody

    @GET
    suspend fun getTestData(@Url urn: String): ResponseBody
}