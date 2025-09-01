package com.leo.paleorecipes.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import com.leo.paleorecipes.data.repository.RecipeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Worker that handles syncing recipes from the API to the local database.
 */
@HiltWorker
class SyncRecipesWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val repository: RecipeRepository,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "SyncRecipesWorker"
        const val WORK_NAME = "sync_recipes_work"

        // Input data keys
        const val KEY_FORCE_REFRESH = "force_refresh"

        // Default backoff policy
        const val BACKOFF_DELAY_MINUTES = 15L
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val forceRefresh = inputData.getBoolean(KEY_FORCE_REFRESH, false)

            Log.d(TAG, "Starting recipe sync. Force refresh: $forceRefresh")

            // Sync recipes from the API
            val result = repository.syncRecipes(forceRefresh)

            if (result.isSuccess) {
                Log.d(TAG, "Recipe sync completed successfully")
                Result.success()
            } else {
                val exception = result.exceptionOrNull()
                Log.e(TAG, "Recipe sync failed: ${exception?.message}", exception)

                // Use exponential backoff for retries
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during recipe sync", e)

            // Use exponential backoff for retries
            Result.retry()
        }
    }

    /**
     * Factory to create [SyncRecipesWorker] instances with Hilt.
     */
    @AssistedFactory
    interface Factory : ChildWorkerFactory {
        override fun create(
            appContext: Context,
            params: WorkerParameters,
        ): SyncRecipesWorker
    }
}
