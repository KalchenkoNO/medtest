package com.ce2af4a3.tests.di

import com.ce2af4a3.tests.data.TestDataRepositoryImpl
import com.ce2af4a3.tests.data.TestingSettingsRepositoryImpl
import com.ce2af4a3.tests.domain.TestData
import com.ce2af4a3.tests.domain.TestDataRepository
import com.ce2af4a3.tests.domain.TestingSettingsRepository
import com.ce2af4a3.tests.utils.Parser
import com.ce2af4a3.tests.utils.TestDataParser
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface TestingModule {
    @Binds
    fun bindTestDataRepository(repository: TestDataRepositoryImpl): TestDataRepository

    @Binds
    fun bindParser(parser: TestDataParser): Parser<String, TestData>

    @Binds
    fun bindTestingSettingsRepository(repository: TestingSettingsRepositoryImpl): TestingSettingsRepository
}