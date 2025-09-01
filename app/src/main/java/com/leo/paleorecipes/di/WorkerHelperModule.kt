package com.leo.paleorecipes.di

import androidx.work.WorkManager
import com.leo.paleorecipes.workers.WorkManagerHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerHelperModule {

    @Provides
    @Singleton
    fun provideWorkManagerHelper(
        workManager: WorkManager,
    ): WorkManagerHelper = WorkManagerHelper(workManager)
}
