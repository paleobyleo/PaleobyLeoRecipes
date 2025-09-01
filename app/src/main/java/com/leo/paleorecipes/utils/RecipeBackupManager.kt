package com.leo.paleorecipes.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.leo.paleorecipes.data.Recipe
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A utility class for backing up and restoring recipes.
 */
@Singleton
class RecipeBackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fileUtils: FileUtils,
) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val tag = "RecipeBackupManager"

    /**
     * Exports all user recipes to a JSON file in the Downloads directory.
     *
     * @param recipes The list of recipes to export
     * @return Result containing the file path if successful, or an error message if failed
     */
    fun exportRecipes(recipes: List<Recipe>): Result<String> {
        return try {
            // Create filename with timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "paleo_recipes_backup_$timestamp.json"
            
            // Try to save using MediaStore API (recommended for Android 10+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val result = saveFileUsingMediaStore(fileName, recipes)
                if (result.isSuccess) {
                    return result
                }
                // If MediaStore fails, fall back to file system approach
            }
            
            // Fallback to file system approach for older Android versions or if MediaStore fails
            saveFileUsingFileSystem(fileName, recipes)
        } catch (e: Exception) {
            Log.e(tag, "Failed to export recipes", e)
            Result.failure(Exception("Failed to export recipes: ${e.message}"))
        }
    }
    
    /**
     * Saves file using MediaStore API (recommended for Android 10+)
     */
    private fun saveFileUsingMediaStore(fileName: String, recipes: List<Recipe>): Result<String> {
        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/PaleoRecipes")
            }
            
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        gson.toJson(recipes, writer)
                    }
                }
                Result.success("File saved to Downloads/PaleoRecipes/$fileName")
            } else {
                Result.failure(Exception("Failed to create file in Downloads"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to save file using MediaStore", e)
            Result.failure(Exception("Failed to save file using MediaStore: ${e.message}"))
        }
    }
    
    /**
     * Saves file using traditional file system approach
     */
    private fun saveFileUsingFileSystem(fileName: String, recipes: List<Recipe>): Result<String> {
        return try {
            // Try to use the public Downloads directory - this is the most accessible location
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            var backupDir: File? = null
            
            if (downloadsDir != null && downloadsDir.exists()) {
                backupDir = File(downloadsDir, "PaleoRecipes")
            }
            
            // If that fails, try app-specific external files directory
            if (backupDir == null || !backupDir.exists()) {
                val appExternalDir = context.getExternalFilesDir(null)
                if (appExternalDir != null) {
                    backupDir = File(appExternalDir, "backups")
                }
            }
            
            // If we still don't have a directory, use internal storage as last resort
            if (backupDir == null) {
                backupDir = File(context.filesDir, "backups")
            }
            
            // Create backup directory if it doesn't exist
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            val backupFile = File(backupDir, fileName)

            // Write recipes to JSON file
            FileWriter(backupFile).use { writer ->
                gson.toJson(recipes, writer)
            }

            Log.d(tag, "Successfully exported ${recipes.size} recipes to ${backupFile.absolutePath}")
            Result.success(backupFile.absolutePath)
        } catch (e: Exception) {
            Log.e(tag, "Failed to export recipes using file system", e)
            Result.failure(Exception("Failed to export recipes using file system: ${e.message}"))
        }
    }

    /**
     * Imports recipes from a JSON file.
     *
     * @param uri The URI of the JSON file to import
     * @return Result containing the list of imported recipes if successful, or an error if failed
     */
    fun importRecipes(uri: Uri): Result<List<Recipe>> {
        return try {
            // Read the file content
            val jsonString = readTextFromUri(uri)
            
            if (jsonString.isNullOrEmpty()) {
                return Result.failure(Exception("Selected file is empty or invalid. Please select a valid backup file."))
            }

            // Log the JSON string for debugging (first 1000 characters)
            Log.d(tag, "JSON content (first 1000 chars): ${jsonString.take(1000)}")
            
            // Try to parse the JSON content
            try {
                // Create TypeToken in a more traditional way
                val recipeListType = object : TypeToken<List<Recipe>>() {}.type
                
                // First, try to parse with the current Recipe structure
                val recipes: List<Recipe> = gson.fromJson(jsonString, recipeListType)
                
                Log.d(tag, "Successfully imported ${recipes.size} recipes from $uri")
                
                if (recipes.isEmpty()) {
                    return Result.failure(Exception("No recipes found in the selected file. Please make sure you're selecting a valid Paleo Recipes backup file."))
                }
                
                Result.success(recipes)
            } catch (e: Exception) {
                // If parsing fails, try to provide more specific error information
                Log.e(tag, "Failed to parse recipes from $uri", e)
                
                // Log the full JSON for debugging
                Log.d(tag, "Full JSON content: $jsonString")
                
                // Check if it's a specific type of error we can handle
                when {
                    e.message?.contains("type token", ignoreCase = true) == true -> {
                        // Try an alternative parsing approach
                        try {
                            // Try parsing as a generic list and then mapping
                            val listType = TypeToken.getParameterized(List::class.java, Map::class.java).type
                            val recipeMaps: List<Map<*, *>> = gson.fromJson(jsonString, listType)
                            
                            // Convert maps to Recipe objects
                            val recipes = recipeMaps.map { map ->
                                Recipe(
                                    id = (map["id"] as? Number)?.toLong() ?: 0L,
                                    title = map["title"] as? String ?: "",
                                    description = map["description"] as? String ?: "",
                                    ingredients = (map["ingredients"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                                    instructions = (map["instructions"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                                    prepTime = (map["prepTime"] as? Number)?.toInt() ?: 0,
                                    cookTime = (map["cookTime"] as? Number)?.toInt() ?: 0,
                                    servings = (map["servings"] as? Number)?.toInt() ?: 1,
                                    imageUrl = map["imageUrl"] as? String ?: "",
                                    category = map["category"] as? String ?: "",
                                    difficulty = map["difficulty"] as? String ?: "",
                                    notes = map["notes"] as? String ?: "",
                                    isUserCreated = map["isUserCreated"] as? Boolean ?: false,
                                    isFavorite = map["isFavorite"] as? Boolean ?: false,
                                    dateAdded = (map["dateAdded"] as? Number)?.toLong() ?: System.currentTimeMillis()
                                )
                            }
                            
                            Log.d(tag, "Successfully imported ${recipes.size} recipes using fallback method")
                            return Result.success(recipes)
                        } catch (fallbackException: Exception) {
                            Log.e(tag, "Fallback parsing also failed", fallbackException)
                            return Result.failure(Exception("Data format incompatibility detected. This backup file may have been created with a different version of the app. Error: ${e.message}"))
                        }
                    }
                    e is JsonSyntaxException -> {
                        return Result.failure(Exception("Invalid JSON format in backup file. The file may be corrupted. Error: ${e.message}"))
                    }
                    e.message?.contains("Expected BEGIN_ARRAY", ignoreCase = true) == true -> {
                        return Result.failure(Exception("Invalid file format. Expected a JSON array of recipes. Error: ${e.message}"))
                    }
                    else -> {
                        return Result.failure(Exception("Failed to import recipes: ${e.message}. This might be due to a data format incompatibility between versions."))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to import recipes from $uri", e)
            Result.failure(Exception("Failed to import recipes: ${e.message}. Please make sure you're selecting a valid JSON backup file."))
        }
    }
    
    /**
     * Reads text content from a URI
     */
    private fun readTextFromUri(uri: Uri): String? {
        return try {
            val resolver = context.contentResolver
            resolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to read text from URI: $uri", e)
            null
        }
    }

    /**
     * Gets the default backup file name.
     *
     * @return The default backup file name
     */
    fun getDefaultBackupFileName(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "paleo_recipes_backup_$timestamp.json"
    }
    
    /**
     * Shows a toast with information about where backup files are stored
     *
     * @param context The context to show the toast
     */
    fun showBackupLocationInfo(context: Context) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val backupPath = if (downloadsDir != null && downloadsDir.exists()) {
            File(downloadsDir, "PaleoRecipes").absolutePath
        } else {
            // Fallback message
            "Downloads/PaleoRecipes/"
        }
        
        val message = "Backup files are saved to:\n$backupPath\n\n" +
                "Look for files named like: paleo_recipes_backup_*.json\n\n" +
                "When importing, navigate to this folder in your file manager:\n" +
                "$backupPath\n\n" +
                "Tip: You can bookmark this location in your file manager for easier access."
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}