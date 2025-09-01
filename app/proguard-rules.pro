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

# Optimize aggressively
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# Reduce size
-repackageclasses ''
-flattenpackagehierarchy