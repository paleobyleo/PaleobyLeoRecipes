package com.leo.paleorecipes

import android.util.Log
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import androidx.work.WorkManager
import com.leo.paleorecipes.data.repository.RecipeRepository
import com.leo.paleorecipes.di.HiltWorkerFactory
import com.leo.paleorecipes.utils.ImageUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class PaleoRecipesApplication : MultiDexApplication(), Configuration.Provider {

    @Inject
    lateinit var repository: RecipeRepository

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    // Determine if we're in debug mode without relying on BuildConfig
    private val isDebug: Boolean by lazy {
        try {
            // Try to get the application info
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            // If we can't determine, assume debug mode
            true
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        }

        // Initialize WorkManager with Hilt
        WorkManager.initialize(this, workManagerConfiguration)

        Timber.d("Application created with WorkManager configuration: $workManagerConfiguration")

        // Temporarily disable AdMob initialization to fix launch issue
        // AdMobUtils.initialize(this)
        // Timber.d("AdMob initialized")

        // Initialize image cache directory
        ImageUtils.initImageCache(this)

        // Clean up orphaned images in the background
        applicationScope.launch {
            try {
                // Get user recipes for image cleanup
                repository.getAllUserRecipes().collect { recipes ->
                    try {
                        // Get all image URLs from user recipes
                        val allImageUrls = recipes
                            .map { it.imageUrl }
                            .filter { it.isNotBlank() }

                        // Clean up orphaned images
                        ImageUtils.cleanupOrphanedImages(applicationContext, allImageUrls)
                        Timber.d("Cleaned up orphaned images")
                    } catch (e: Exception) {
                        Timber.e(e, "Error cleaning up images: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error setting up image cleanup: ${e.message}")
            }
        }
    }
}
