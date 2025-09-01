# AdMob Integration Guide

This document explains how AdMob has been integrated into the Paleo by Leo Recipes app.

## AdMob Account Information

The app is now configured with your actual AdMob App ID:
- **App ID**: ca-app-pub-3031011439814812~3315442670

## Implementation Details

### 1. Dependencies

The AdMob SDK has been added to the app's build.gradle file:

```gradle
implementation 'com.google.android.gms:play-services-ads:23.2.0'
```

### 2. AndroidManifest.xml Configuration

The AdMob App ID has been added to the AndroidManifest.xml with the correct format:

```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-3031011439814812~3315442670"/>
```

**Important**: The App ID must follow the format `ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYYYYYYYY` where:
- XXXXXXXXXXXXXXXX is your publisher ID
- YYYYYYYYYYYYYYYY is a unique identifier for your app

### 3. AdMob Initialization

AdMob is initialized in the Application class (`PaleoRecipesApplication.kt`) using the `AdMobUtils` helper class:

```kotlin
AdMobUtils.initialize(this)
```

### 4. Utility Classes

Several utility classes have been created to manage different types of ads:

1. **AdMobManager.kt** - A comprehensive manager for all ad types (banner, interstitial, rewarded)
2. **AdMobUtils.kt** - Utility class for AdMob initialization
3. **AdUtils.kt** - Helper class for interstitial ads
4. **AdMobBanner.kt** - Compose component for banner ads

### 5. Ad Implementation

#### Banner Ads
Banner ads are implemented using a custom Composable component `AdMobBanner` which is used in the main screen.

#### Interstitial Ads
Interstitial ads are shown at key user interaction points:
- When saving a recipe in AddEditRecipeActivity
- When exporting recipes in RecipeListActivity

## Testing

For testing purposes, the app uses Google's test ad unit IDs when running in debug mode:

- **Test Banner Ad Unit ID**: ca-app-pub-3940256099942544/6300978111
- **Test Interstitial Ad Unit ID**: ca-app-pub-3940256099942544/1033173712

## Production vs Development

The app automatically switches between test and production ad unit IDs based on the build type:
- Debug builds use test ad unit IDs
- Release builds use your production ad unit IDs

## Important Notes

1. Make sure to replace the placeholder ad unit IDs with your actual IDs before publishing
2. Test ads thoroughly to ensure proper functionality
3. Monitor ad performance through the AdMob console
4. Ensure compliance with AdMob policies and guidelines

## Troubleshooting

If ads are not displaying:
1. Check that the AdMob App ID is correctly configured in AndroidManifest.xml with the proper format
2. Verify that ad unit IDs are correct for the build type (debug vs release)
3. Ensure the device has internet connectivity
4. Check the logcat for any AdMob-related error messages

If the app crashes on launch with "Invalid application ID" error:
1. Verify that the App ID in AndroidManifest.xml follows the correct format
2. Use Google's test App ID (ca-app-pub-3940256099942544~3347511713) for testing
3. Replace with your actual App ID from the AdMob console when ready for production