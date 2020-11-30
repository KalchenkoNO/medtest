package com.ce2af4a3.tests.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.ce2af4a3.tests.utils.AndroidResourceProvider
import com.ce2af4a3.tests.utils.ResourceProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences = with(context) {
        return getSharedPreferences(packageName, Context.MODE_PRIVATE)
    }

    @Provides
    fun providesResourcesProvider(resourceProvider: AndroidResourceProvider): ResourceProvider {
        return resourceProvider
    }

    @Provides
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }
}