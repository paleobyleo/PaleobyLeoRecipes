package com.leo.paleorecipes.di

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module for providing WorkManager related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerModule {

    companion object {
        @Provides
        @Singleton
        fun provideWorkManager(
            @ApplicationContext context: Context,
        ): WorkManager {
            // Note: WorkManager is initialized in PaleoRecipesApplication
            // This just provides the instance for injection
            return WorkManager.getInstance(context)
        }
    }
}
