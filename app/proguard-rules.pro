# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep data classes and their members for Room database
-keep class com.leo.paleorecipes.data.** { *; }
-keep class com.leo.paleorecipes.data.**$* { *; }

# Keep ViewModel classes
-keep class com.leo.paleorecipes.viewmodel.** { *; }

# Keep Hilt generated classes
-keep class com.leo.paleorecipes.Hilt_** { *; }
-keep class com.leo.paleorecipes.*_HiltModules { *; }
-keep class com.leo.paleorecipes.*_Factory { *; }

# Keep data binding classes
-keep class androidx.databinding.** { *; }
-keep class androidx.databinding.library.** { *; }

# Keep Compose related classes
-keep class androidx.compose.** { *; }
-keep class kotlin.** { *; }

# Keep Retrofit and OkHttp classes
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep interface retrofit2.** { *; }

# Keep Gson classes
-keep class com.google.gson.** { *; }

# Keep ML Kit classes
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep Coil classes
-keep class coil.** { *; }

# Keep data class constructors and members
-keepclassmembers class com.leo.paleorecipes.data.** {
    <init>(...);
    <fields>;
    <methods>;
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep annotation classes
-keep class * extends java.lang.annotation.Annotation

# Keep parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep activities
-keep class com.leo.paleorecipes.**Activity { *; }

# Keep all classes in the main package to avoid R8 issues
-keep class com.leo.paleorecipes.** { *; }

# Add the missing rules suggested by R8
-dontwarn com.leo.paleorecipes.AboutPaleoActivity
-dontwarn com.leo.paleorecipes.AddEditRecipeActivity
-dontwarn com.leo.paleorecipes.AddRecipeActivity
-dontwarn com.leo.paleorecipes.ComposableSingletons$MainActivityComposeKt
-dontwarn com.leo.paleorecipes.ComposableSingletons$OcrScanActivityKt
-dontwarn com.leo.paleorecipes.adapter.IngredientsAdapter
-dontwarn com.leo.paleorecipes.adapter.RecipesAdapter
-dontwarn com.leo.paleorecipes.data.AppDatabase$Companion
-dontwarn com.leo.paleorecipes.data.AppDatabase
-dontwarn com.leo.paleorecipes.data.Converters
-dontwarn com.leo.paleorecipes.data.Recipe
-dontwarn com.leo.paleorecipes.data.RecipeDao
-dontwarn com.leo.paleorecipes.data.api.ApiClient
-dontwarn com.leo.paleorecipes.data.api.SpoonacularApiService
-dontwarn com.leo.paleorecipes.data.local.dao.IngredientDao
-dontwarn com.leo.paleorecipes.data.local.entity.IngredientEntity
-dontwarn com.leo.paleorecipes.data.repository.IngredientRepository
-dontwarn com.leo.paleorecipes.data.repository.IngredientRepositoryImpl
-dontwarn com.leo.paleorecipes.data.repository.RecipeRepository
-dontwarn com.leo.paleorecipes.data.repository.RecipeRepositoryImpl

# Optimize aggressively but avoid conflicts
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# Reduce size but avoid conflicts with repackageclasses
# Comment out the conflicting directives
# -repackageclasses ''
# -flattenpackagehierarchy