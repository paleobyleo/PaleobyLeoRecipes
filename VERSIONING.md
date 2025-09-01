# Paleo Recipes Versioning System

This document explains how the versioning system works for the Paleo Recipes app.

## Version Format

The app uses semantic versioning with the format: `MAJOR.MINOR.PATCH`

- **MAJOR**: Significant changes or new features that may break compatibility
- **MINOR**: New features that are backward compatible
- **PATCH**: Bug fixes and minor improvements

## Version Files

1. **version.properties**: Stores the current version numbers (MAJOR, MINOR, PATCH)
2. **version_history.json**: Contains detailed version history with release dates and changes
3. **versioning.gradle**: Gradle script that handles version calculations and updates

## How Versions Are Calculated

The version code is calculated as: `MAJOR * 10000 + MINOR * 100 + PATCH`

For example:
- Version 1.0.0 = 10000
- Version 1.2.3 = 10203
- Version 2.0.0 = 20000

The version name is simply: `MAJOR.MINOR.PATCH`

## Managing Versions

### Incrementing Versions

Use the provided batch script to increment versions:

```bash
# Increment patch version (1.0.0 -> 1.0.1)
increment_version.bat patch

# Increment minor version (1.0.1 -> 1.1.0)
increment_version.bat minor

# Increment major version (1.1.0 -> 2.0.0)
increment_version.bat major
```

### Manual Version Management

You can also manually edit the `version.properties` file:

```properties
VERSION_MAJOR=1
VERSION_MINOR=0
VERSION_PATCH=1
```

## Building with New Versions

After incrementing the version, build the app as usual:

```bash
# For debug builds
gradlew assembleDebug

# For release builds
gradlew assembleRelease
```

The APK files will be named with the version information:
- Debug: `paleobyleorecipes-1.0.1-debug.apk`
- Release: `paleobyleorecipes-1.0.1.apk`

## Version History

The `version_history.json` file in the assets folder maintains a record of all versions:

```json
{
  "currentVersion": {
    "versionCode": 10000,
    "versionName": "1.0.0",
    "releaseDate": "2025-08-30",
    "changes": ["Initial release"]
  },
  "versionHistory": [
    {
      "versionCode": 10000,
      "versionName": "1.0.0",
      "releaseDate": "2025-08-30",
      "changes": ["Initial release"]
    }
  ]
}
```

## Accessing Version Information in Code

You can access version information in your Kotlin code using the `VersionManager` class:

```kotlin
val versionManager = VersionManager(context)
val versionInfo = versionManager.getCurrentVersionInfo()
val versionString = versionManager.getFormattedVersionString()
```

## Displaying Version Information

The version information is automatically displayed in the Settings screen of the app.

## Best Practices

1. Increment the version before each release
2. Update the version history with release notes
3. Use patch versions for bug fixes
4. Use minor versions for new features
5. Use major versions for significant changes