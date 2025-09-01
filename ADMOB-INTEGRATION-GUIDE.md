# AdMob Integration Guide for Paleo Recipes

This guide explains how to integrate Google AdMob into the Paleo Recipes application.

## Prerequisites

1. Google AdMob account
2. AdMob App ID
3. Ad Unit IDs for banner, interstitial, and rewarded ads

## Integration Steps

### 1. Add AdMob SDK to build.gradle

The AdMob SDK has already been added to your `app/build.gradle` file:

```gradle
dependencies {
    // Google AdMob SDK
    implementation 'com.google.android.gms:play-services-ads:23.2.0'
}
```

### 2. Add App ID to AndroidManifest.xml

The AdMob App ID has been added to your `AndroidManifest.xml`:

```xml
<!-- Google AdMob App ID -->
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="YOUR_ADMOB_APP_ID" />
```

**Important**: Replace `YOUR_ADMOB_APP_ID` with your actual AdMob App ID.

### 3. AdMob Utility Class

An `AdMobUtils.kt` utility class has been created in the `utils` package to help manage different types of ads:

- Banner ads
- Interstitial ads
- Rewarded ads

### 4. Composable Banner Component

A `AdMobBanner.kt` composable component has been created in the `ui/components` package for easy integration of banner ads in Jetpack Compose screens.

### 5. Initialization in Application Class

AdMob is automatically initialized in the `PaleoRecipesApplication.kt` class:

```kotlin
// Initialize AdMob
AdMobUtils.initialize(this)
```

## Configuration

### 1. Update Ad Unit IDs

In `AdMobUtils.kt`, replace the placeholder ad unit IDs with your actual IDs:

```kotlin
// Replace these with your actual ad unit IDs
private const val BANNER_AD_UNIT_ID = "YOUR_BANNER_AD_UNIT_ID"
private const val INTERSTITIAL_AD_UNIT_ID = "YOUR_INTERSTITIAL_AD_UNIT_ID"
private const val REWARDED_AD_UNIT_ID = "YOUR_REWARDED_AD_UNIT_ID"
```

In `AdMobBanner.kt`, update the ad unit ID:

```kotlin
adUnitId: String = if (BuildConfig.DEBUG) {
    "ca-app-pub-3940256099942544/6300978111" // Test ad unit ID
} else {
    "YOUR_BANNER_AD_UNIT_ID" // Replace with your actual ad unit ID
}
```

### 2. Test Ads vs Production Ads

The integration automatically uses test ad unit IDs when in debug mode and your production ad unit IDs when in release mode.

## Usage Examples

### Banner Ads

Banner ads are already integrated into the `MainActivityCompose.kt` at the bottom of the screen:

```kotlin
// AdMob Banner Ad
AdMobBanner(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
)
```

### Interstitial Ads

To load and show interstitial ads:

```kotlin
// Load an interstitial ad
AdMobUtils.loadInterstitialAd(context) { interstitialAd ->
    // Ad loaded callback
}

// Show an interstitial ad
AdMobUtils.showInterstitialAd(activity) {
    // Ad dismissed callback
}
```

### Rewarded Ads

To load and show rewarded ads:

```kotlin
// Load a rewarded ad
AdMobUtils.loadRewardedAd(context) { rewardedAd ->
    // Ad loaded callback
}

// Show a rewarded ad
AdMobUtils.showRewardedAd(activity, 
    onAdDismissed = {
        // Ad dismissed callback
    },
    onUserEarnedReward = { reward ->
        // User earned reward callback
    }
)
```

## Testing

1. Use the test ad unit IDs provided by Google during development
2. Test on both emulators and physical devices
3. Verify that ads load and display correctly
4. Test ad click functionality
5. Ensure proper error handling

## Best Practices

1. **Ad Placement**: Place ads where they don't interfere with user experience
2. **Ad Frequency**: Don't show ads too frequently to avoid user frustration
3. **Error Handling**: Always implement proper error handling for ad loading failures
4. **User Experience**: Consider implementing rewarded ads for optional features
5. **Compliance**: Follow Google's ad policies and guidelines

## Troubleshooting

### Common Issues

1. **Ads not showing**: Check that you're using the correct ad unit IDs
2. **Initialization errors**: Verify that the App ID is correctly set in AndroidManifest.xml
3. **Network issues**: Ensure the device has internet connectivity
4. **Version conflicts**: Make sure you're using compatible versions of Google Play Services

### Debugging

Enable verbose logging during development to troubleshoot ad issues:

```kotlin
MobileAds.enableLog()
```

## GDPR and Privacy

Ensure your app complies with GDPR and other privacy regulations:

1. Implement a consent management system
2. Provide clear privacy policy
3. Allow users to opt out of personalized ads
4. Handle user data appropriately

## Resources

- [Google AdMob Documentation](https://developers.google.com/admob/android/quick-start)
- [AdMob Policy Guidelines](https://support.google.com/admob/answer/6128543)
- [Google Mobile Ads SDK](https://developers.google.com/admob/android/sdk)

## Support

For issues with AdMob integration, refer to the official Google AdMob documentation or contact Google AdMob support.