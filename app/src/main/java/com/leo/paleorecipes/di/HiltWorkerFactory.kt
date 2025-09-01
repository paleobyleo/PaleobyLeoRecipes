package com.leo.paleorecipes.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.leo.paleorecipes.workers.ChildWorkerFactory
import com.leo.paleorecipes.workers.SyncRecipesWorker
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * A [WorkerFactory] that delegates worker creation to Hilt using the [ChildWorkerFactory] interface.
 * This implementation supports multi-binding of worker factories using a String key.
 */
@Singleton
class HiltWorkerFactory @Inject constructor(
    private val workerFactories: Map<String, @JvmSuppressWildcards Provider<ChildWorkerFactory>>,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        // Find the factory by class name
        val foundEntry = workerFactories.entries.find {
            it.key == workerClassName || Class.forName(it.key).name == workerClassName
        }

        val factoryProvider = foundEntry?.value
            ?: throw IllegalArgumentException("Unknown worker class name: $workerClassName")

        return factoryProvider.get().create(appContext, workerParameters)
    }
}

/**
 * Module for providing the [HiltWorkerFactory].
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerFactoryModule {

    companion object {
        private const val SYNC_RECIPES_WORKER = "com.leo.paleorecipes.workers.SyncRecipesWorker"

        @Provides
        @Singleton
        fun provideHiltWorkerFactory(
            workerFactories: Map<String, @JvmSuppressWildcards Provider<ChildWorkerFactory>>,
        ): WorkerFactory {
            return HiltWorkerFactory(workerFactories)
        }
    }

    @Binds
    @IntoMap
    @StringKey(SYNC_RECIPES_WORKER)
    abstract fun bindSyncRecipesWorkerFactory(factory: SyncRecipesWorker.Factory): ChildWorkerFactory
}
