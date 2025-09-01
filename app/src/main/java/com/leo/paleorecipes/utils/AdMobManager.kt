package com.leo.paleorecipes.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.leo.paleorecipes.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for handling AdMob ads in the application.
 * This class handles initialization, loading, and displaying of various ad formats.
 */
@Singleton
class AdMobManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var isInitialized = false
    
    private val tag = "AdMobManager"

    /**
     * Initialize AdMob with the application context.
     * This should be called once during app startup.
     */
    fun initialize() {
        if (isInitialized) return
        
        try {
            MobileAds.initialize(context) { initializationStatus ->
                val status = initializationStatus.adapterStatusMap
                for ((key, value) in status) {
                    Log.d(tag, "Adapter $key: ${value.initializationState}")
                }
                isInitialized = true
                Log.d(tag, "AdMob initialized successfully")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to initialize AdMob", e)
        }
    }

    /**
     * Load an interstitial ad.
     * @param adUnitId The ad unit ID for the interstitial ad
     * @param onAdLoaded Callback when ad is loaded successfully
     * @param onAdFailedToLoad Callback when ad fails to load
     */
    fun loadInterstitialAd(
        adUnitId: String,
        onAdLoaded: (() -> Unit)? = null,
        onAdFailedToLoad: ((String) -> Unit)? = null
    ) {
        if (!isInitialized) {
            Log.w(tag, "AdMob not initialized. Call initialize() first.")
            onAdFailedToLoad?.invoke("AdMob not initialized")
            return
        }

        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                this@AdMobManager.interstitialAd = interstitialAd
                Log.d(tag, "Interstitial ad loaded successfully")
                onAdLoaded?.invoke()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(tag, "Interstitial ad failed to load: ${adError.message}")
                this@AdMobManager.interstitialAd = null
                onAdFailedToLoad?.invoke(adError.message)
            }
        })
    }

    /**
     * Show an interstitial ad if it's loaded.
     * @param activity The current activity
     * @param onAdClosed Callback when ad is closed
     * @param onAdFailedToShow Callback when ad fails to show
     */
    fun showInterstitialAd(
        activity: Activity,
        onAdClosed: (() -> Unit)? = null,
        onAdFailedToShow: ((String) -> Unit)? = null
    ) {
        interstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(tag, "Interstitial ad dismissed")
                    interstitialAd = null
                    onAdClosed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(tag, "Interstitial ad failed to show: ${adError.message}")
                    interstitialAd = null
                    onAdFailedToShow?.invoke(adError.message)
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(tag, "Interstitial ad showed")
                }
            }
            
            ad.show(activity)
        } ?: run {
            Log.w(tag, "Interstitial ad not loaded")
            onAdFailedToShow?.invoke("Interstitial ad not loaded")
        }
    }

    /**
     * Load a rewarded ad.
     * @param adUnitId The ad unit ID for the rewarded ad
     * @param onAdLoaded Callback when ad is loaded successfully
     * @param onAdFailedToLoad Callback when ad fails to load
     */
    fun loadRewardedAd(
        adUnitId: String,
        onAdLoaded: (() -> Unit)? = null,
        onAdFailedToLoad: ((String) -> Unit)? = null
    ) {
        if (!isInitialized) {
            Log.w(tag, "AdMob not initialized. Call initialize() first.")
            onAdFailedToLoad?.invoke("AdMob not initialized")
            return
        }

        val adRequest = AdRequest.Builder().build()
        
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(rewardedAd: RewardedAd) {
                this@AdMobManager.rewardedAd = rewardedAd
                Log.d(tag, "Rewarded ad loaded successfully")
                onAdLoaded?.invoke()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(tag, "Rewarded ad failed to load: ${adError.message}")
                this@AdMobManager.rewardedAd = null
                onAdFailedToLoad?.invoke(adError.message)
            }
        })
    }

    /**
     * Show a rewarded ad if it's loaded.
     * @param activity The current activity
     * @param onAdClosed Callback when ad is closed
     * @param onAdFailedToShow Callback when ad fails to show
     * @param onUserEarnedReward Callback when user earns reward
     */
    fun showRewardedAd(
        activity: Activity,
        onAdClosed: (() -> Unit)? = null,
        onAdFailedToShow: ((String) -> Unit)? = null,
        onUserEarnedReward: ((RewardItem) -> Unit)? = null
    ) {
        rewardedAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(tag, "Rewarded ad dismissed")
                    rewardedAd = null
                    onAdClosed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(tag, "Rewarded ad failed to show: ${adError.message}")
                    rewardedAd = null
                    onAdFailedToShow?.invoke(adError.message)
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(tag, "Rewarded ad showed")
                }
            }
            
            ad.show(activity) { rewardItem ->
                Log.d(tag, "User earned reward: ${rewardItem.type} ${rewardItem.amount}")
                onUserEarnedReward?.invoke(rewardItem)
            }
        } ?: run {
            Log.w(tag, "Rewarded ad not loaded")
            onAdFailedToShow?.invoke("Rewarded ad not loaded")
        }
    }

    /**
     * Load a banner ad into a FrameLayout container.
     * @param activity The current activity
     * @param adContainer The FrameLayout container for the banner ad
     * @param adUnitId The ad unit ID for the banner ad
     */
    fun loadBannerAd(activity: Activity, adContainer: FrameLayout, adUnitId: String) {
        if (!isInitialized) {
            Log.w(tag, "AdMob not initialized. Call initialize() first.")
            return
        }

        try {
            val adView = AdView(activity)
            adView.adUnitId = adUnitId
            adView.setAdSize(AdSize.BANNER)
            
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
            
            adContainer.removeAllViews()
            adContainer.addView(adView)
            
            Log.d(tag, "Banner ad loaded successfully")
        } catch (e: Exception) {
            Log.e(tag, "Failed to load banner ad", e)
        }
    }

    /**
     * Check if an interstitial ad is loaded and ready to show.
     */
    fun isInterstitialAdLoaded(): Boolean = interstitialAd != null

    /**
     * Check if a rewarded ad is loaded and ready to show.
     */
    fun isRewardedAdLoaded(): Boolean = rewardedAd != null
}