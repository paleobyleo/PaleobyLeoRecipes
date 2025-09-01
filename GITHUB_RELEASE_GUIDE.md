# How to Create a GitHub Release for PaleobyLeoRecipes

## Prerequisites
1. You need a GitHub account
2. You should be logged into your GitHub account
3. You need to have the release APK file

## Steps to Create a Release

### 1. Navigate to the Releases Page
1. Go to your repository: https://github.com/paleobyleo/PaleobyLeoRecipes
2. Click on the "Releases" tab on the right side of the repository page
3. Click the "Draft a new release" button

### 2. Fill in Release Information
1. **Tag version**: Enter `v1.0.2`
2. **Target**: Select `main` branch
3. **Release title**: Enter `Release v1.0.2`
4. **Description**: Copy and paste the content from [RELEASE_NOTES.md](file:///c:/Users/leoja/AndroidStudioProjects/PaleobyLeoRecipes/RELEASE_NOTES.md) or use the content below:

```
# PaleobyLeoRecipes v1.0.2

## Release Date
September 1, 2025

## What's New
This release includes several important fixes and improvements to enhance your recipe management experience:

### Bug Fixes
- Fixed recipe import functionality that was failing with "type token" errors
- Resolved compatibility issues with importing old recipe backup files
- Enhanced error handling for better user feedback

### Improvements
- Modernized Android file storage APIs for better compatibility with Android 10+ devices
- Improved backup file location to Downloads/PaleoRecipes/ for easier access
- Enhanced UI with consistent saddle brown color scheme across all screens
- Better error messages and user guidance throughout the app

### Features
- Complete recipe management (add, edit, delete, view)
- Recipe import/export functionality
- OCR scanning for recipe images
- Dark mode support
- Recipe categorization and favorites
- AdMob integration for monetization - Added banner and interstitial ads

## AdMob Integration
This release includes AdMob integration with the following ad placements:
- Banner ads at the bottom of the main screen
- Interstitial ads when saving recipes
- Interstitial ads when exporting recipes

## Installation
To install this release:
1. Download the APK file (paleobyleorecipes-1.0.2.apk) from the Assets section below
2. Enable "Install from unknown sources" in your device settings
3. Open the APK file and follow the installation prompts

## Compatibility
- Android 5.0 (API level 21) and higher
- Supports both traditional file system and scoped storage (Android 10+)

## Support
If you encounter any issues with this release, please:
1. Check the FAQ in the About section of the app
2. Report issues on GitHub: https://github.com/paleobyleo/PaleobyLeoRecipes/issues
3. Contact support through the app's feedback form
```

### 3. Upload Release Assets
This is the critical step to ensure users can download the APK:

1. Click "Attach binaries by dropping them here or selecting them"
2. Navigate to and select the APK file from: `app/build/outputs/apk/release/paleobyleorecipes-1.0.2.apk`
3. **Important**: Make sure the APK file appears in the "Assets" section
4. You should see two files in Assets:
   - `paleobyleorecipes-1.0.2.apk` (the installation file)
   - `Source code (zip)` (automatically added by GitHub)

### 4. Publish Release
1. Make sure "This is a pre-release" is unchecked
2. Click "Publish release"

## Release Verification
After publishing, verify that:
1. The release appears on the releases page
2. The APK file is downloadable from the Assets section
3. The release notes are properly formatted
4. The tag `v1.0.2` exists in the tags list

## Troubleshooting
If you only see "Source code (zip)" and not the APK file:
1. Edit the release by clicking the pencil icon
2. Scroll down to the "Assets" section
3. Click "Delete" next to any existing assets if needed
4. Re-upload the APK file by clicking "Attach binaries..."
5. Select the APK file from `app/build/outputs/apk/release/paleobyleorecipes-1.0.2.apk`
6. Save the changes

## Post-Release Steps
1. Test the download link to ensure the APK works correctly
2. Update any documentation or websites that reference the release
3. Announce the release on relevant channels if applicable