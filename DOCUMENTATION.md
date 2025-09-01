# Paleo Recipes - Technical Documentation

## Table of Contents
1. [Architecture](#architecture)
2. [Project Structure](#project-structure)
3. [Key Features](#key-features)
4. [Data Model](#data-model)
5. [UI Components](#ui-components)
6. [Dependencies](#dependencies)
7. [Build Configuration](#build-configuration)
8. [Testing](#testing)
9. [Deployment](#deployment)

## Architecture

The Paleo Recipes app follows the MVVM (Model-View-ViewModel) architectural pattern with a clean separation of concerns:

### Model Layer
- **Data Entities**: Recipe data class with fields for title, description, ingredients, etc.
- **Database**: Room persistence library for local data storage
- **Repository**: Abstracts data sources and provides a clean API for data access

### ViewModel Layer
- **RecipeViewModel**: Manages UI-related data and business logic
- **LiveData**: Observes data changes and updates the UI accordingly
- **Hilt**: Dependency injection for clean and testable code

### View Layer
- **Jetpack Compose**: Modern Android UI toolkit for declarative UI
- **Activities**: MainActivityCompose as the main entry point
- **Navigation**: Compose Navigation for screen transitions

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/leo/paleorecipes/
│   │   │   ├── data/
│   │   │   │   ├── Recipe.kt
│   │   │   │   └── RecipeDao.kt
│   │   │   ├── repository/
│   │   │   │   └── RecipeRepository.kt
│   │   │   ├── viewmodel/
│   │   │   │   └── RecipeViewModel.kt
│   │   │   ├── ui/
│   │   │   │   └── theme/
│   │   │   ├── MainActivityCompose.kt
│   │   │   ├── AddEditRecipeActivity.kt
│   │   │   ├── RecipeDetailActivity.kt
│   │   │   ├── OcrScanActivity.kt
│   │   │   └── PaleoRecipesApplication.kt
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── test/
└── build.gradle
```

## Key Features

### Recipe Management
- Add, edit, and delete recipes
- Mark recipes as favorites
- View recipe details

### Search & Filter
- Search recipes by title
- Filter by favorites
- Clear search functionality

### OCR Scanning
- Capture recipes from camera
- Import recipes from gallery
- Text recognition using ML Kit

### Print Functionality
- Generate printable versions of recipes
- WebView-based printing
- PDF generation support

### UI Features
- Dark theme implementation
- Responsive design
- Material Design 3 components
- Compose-based UI

## Data Model

### Recipe Entity
```kotlin
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val ingredients: String = "",
    val instructions: String = "",
    val category: String = "",
    val cookTime: Int = 0,
    val isFavorite: Boolean = false
)
```

### Database Operations
- Insert new recipes
- Update existing recipes
- Delete recipes
- Query all recipes
- Query favorite recipes
- Search recipes by title

## UI Components

### Main Screen
- Recipe list with cards
- Search bar
- Favorites toggle
- Add recipe button
- OCR scan button

### Recipe Card
- Title display
- Category and cook time buttons
- Description text
- Favorite indicator
- Action buttons (edit, print, delete, view)

### Add/Edit Recipe Screen
- Form fields for all recipe properties
- Save and cancel actions
- OCR integration

### Recipe Detail Screen
- Full recipe information
- Print functionality
- Edit option

### OCR Scan Screen
- Camera preview
- Gallery import
- Text recognition results
- Recipe creation from scanned text

## Dependencies

### Core Android
- `androidx.core:core-ktx` - Kotlin extensions
- `androidx.appcompat:appcompat` - Support library
- `com.google.android.material:material` - Material components

### Jetpack Compose
- `androidx.compose.material3:material3` - Material Design 3
- `androidx.compose.ui:ui` - Core UI components
- `androidx.activity:activity-compose` - Compose integration

### Architecture
- `androidx.lifecycle:lifecycle-viewmodel-compose` - ViewModel for Compose
- `androidx.lifecycle:lifecycle-livedata-ktx` - LiveData
- `androidx.room:room-runtime` - Database
- `com.google.dagger:hilt-android` - Dependency injection

### Functionality
- `com.google.mlkit:text-recognition` - OCR scanning
- `io.coil-kt:coil-compose` - Image loading
- `com.squareup.retrofit2:retrofit` - HTTP client (if used)
- `com.google.code.gson:gson` - JSON parsing

## Build Configuration

### Gradle Configuration
- Kotlin 1.9.21
- Java 17
- Android API 35 (compileSdk)
- Minimum API 24 (minSdk)
- Target API 35 (targetSdk)

### Release Build
- Minification enabled
- ProGuard rules applied
- R8 full mode optimization
- Signing configuration

### Debug Build
- Debuggable enabled
- Application ID suffix
- Logging enabled

## Testing

### Unit Tests
- Repository layer tests
- ViewModel tests
- Data model tests

### Instrumentation Tests
- UI interaction tests
- Navigation tests
- Database integration tests

### Test Dependencies
- JUnit 4
- AndroidX Test
- Espresso
- Kotlin Coroutines Test

## Deployment

### Release Process
1. Update version code and name in build.gradle
2. Run release build script
3. Sign APK with release key
4. Upload to Google Play Console or GitHub Releases

### GitHub Publishing
1. Create release branch
2. Update documentation
3. Tag release version
4. Push to repository
5. Create GitHub release

### Version Management
- Semantic versioning (MAJOR.MINOR.PATCH)
- Version code incremented with each release
- Changelog maintained for each version