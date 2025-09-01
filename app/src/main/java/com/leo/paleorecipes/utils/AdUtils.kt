package com.leo.paleorecipes.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.leo.paleorecipes.BuildConfig

object AdUtils {
    private const val TAG = "AdUtils"
    private var interstitialAd: InterstitialAd? = null
    
    // Test ad unit ID for development
    private const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    
    // Replace with your actual ad unit ID - using your provided ID
    private const val PROD_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3031011439814812/1033173712"
    
    private val adUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_INTERSTITIAL_AD_UNIT_ID else PROD_INTERSTITIAL_AD_UNIT_ID
    
    /**
     * Load an interstitial ad
     */
    fun loadInterstitialAd(context: Context) {
        try {
            val adRequest = AdRequest.Builder().build()
            
            InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded successfully")
                    interstitialAd = ad
                }
                
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Interstitial ad failed to load: ${adError.message}")
                    interstitialAd = null
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error loading interstitial ad", e)
        }
    }
    
    /**
     * Show an interstitial ad if it's loaded
     */
    fun showInterstitialAd(activity: Activity): Boolean {
        interstitialAd?.let { ad ->
            ad.show(activity)
            // Load a new ad for next time
            loadInterstitialAd(activity)
            return true
        }
        return false
    }
    
    /**
     * Check if an interstitial ad is loaded
     */
    fun isInterstitialAdLoaded(): Boolean {
        return interstitialAd != null
    }
}