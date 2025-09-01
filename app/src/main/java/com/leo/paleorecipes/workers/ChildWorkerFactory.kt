package com.leo.paleorecipes.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

/**
 * Factory interface for creating workers with Hilt injection.
 * This is needed for Hilt to be able to inject dependencies into workers.
 */
interface ChildWorkerFactory {
    /**
     * Creates a new instance of a [ListenableWorker].
     *
     * @param context The application context.
     * @param workerParams Parameters to setup the internal state of the worker.
     * @return A new instance of [ListenableWorker].
     */
    fun create(
        context: Context,
        workerParams: WorkerParameters,
    ): ListenableWorker
}
