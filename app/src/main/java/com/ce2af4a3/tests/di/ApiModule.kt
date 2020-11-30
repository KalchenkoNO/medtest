package com.ce2af4a3.tests.di

import com.ce2af4a3.tests.data.TestsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

@Module
@InstallIn(ApplicationComponent::class)
class ApiModule {
    @Provides
    fun provideRetrofit() = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()

    @Provides
    fun provideApi(retrofit: Retrofit) = retrofit.create(TestsApi::class.java)

    companion object {
        const val URL = "https://www.cpkmetod.ru/"
    }
}