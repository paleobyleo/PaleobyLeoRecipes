package com.leo.paleorecipes.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestModule {
    @Provides
    @Singleton
    fun provideTestString(): String {
        return "Test String from Hilt"
    }
}
