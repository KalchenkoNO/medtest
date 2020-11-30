package com.ce2af4a3.tests.di

import com.ce2af4a3.tests.domain.Test
import com.ce2af4a3.tests.domain.TestListingRepository
import com.ce2af4a3.tests.data.TestsListingRepositoryImpl
import com.ce2af4a3.tests.utils.Parser
import com.ce2af4a3.tests.utils.TestsListParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class TestsListModule {
    @Provides
    fun bindTestsListRepository(repository: TestsListingRepositoryImpl): TestListingRepository {
        return repository
    }

    @Provides
    fun bindTestsListParser(parser: TestsListParser): Parser<String, List<Test>> {
        return parser
    }
}