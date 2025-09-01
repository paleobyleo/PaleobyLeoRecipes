package com.leo.paleorecipes.workers

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerHelper @Inject constructor(
    private val workManager: WorkManager,
) {

    /**
     * Schedule a one-time sync of recipes
     * @param forceRefresh If true, will ignore cache and force a refresh from the network
     */
    fun scheduleRecipeSync(forceRefresh: Boolean = false) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = workDataOf(
            SyncRecipesWorker.KEY_FORCE_REFRESH to forceRefresh,
        )

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncRecipesWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag(SyncRecipesWorker.WORK_NAME)
            .build()

        workManager.enqueueUniqueWork(
            SyncRecipesWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            syncWorkRequest,
        )
    }

    /**
     * Cancel any pending recipe syncs
     */
    fun cancelRecipeSync() {
        workManager.cancelAllWorkByTag(SyncRecipesWorker.WORK_NAME)
    }
}
