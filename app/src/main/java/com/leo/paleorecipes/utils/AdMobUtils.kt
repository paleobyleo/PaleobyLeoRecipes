package com.leo.paleorecipes.utils

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds
import timber.log.Timber

/**
 * Utility class for AdMob initialization.
 */
object AdMobUtils {
    private const val TAG = "AdMobUtils"
    private var isInitialized = false

    /**
     * Initialize AdMob with proper error handling.
     * @param context The application context
     */
    fun initialize(context: Context) {
        if (isInitialized) {
            Timber.d("AdMob already initialized")
            return
        }

        try {
            MobileAds.initialize(context) { initializationStatus ->
                val status = initializationStatus.adapterStatusMap
                for ((key, value) in status) {
                    Timber.d("Adapter $key: ${value.initializationState}")
                }
                isInitialized = true
                Timber.d("AdMob initialized successfully")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize AdMob: ${e.message}")
            // Gracefully handle the error - app should continue to function without ads
        }
    }

    /**
     * Check if AdMob is initialized.
     */
    fun isInitialized(): Boolean = isInitialized
}