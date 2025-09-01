package com.leo.paleorecipes.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ImageUtils {
    private const val TAG = "ImageUtils"
    private const val IMAGE_DIR = "recipe_images"
    private var imageCacheDir: File? = null

    /**
     * Initialize the image cache directory
     */
    fun initImageCache(context: Context) {
        val cacheDir = File(context.filesDir, IMAGE_DIR)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        imageCacheDir = cacheDir
    }

    /**
     * Saves an image from a content URI to the app's internal storage
     * @return The path to the saved image, or null if saving failed
     */
    fun saveImageToInternalStorage(context: Context, imageUri: Uri): String? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "recipe_image_$timeStamp.jpg"
            val storageDir = File(context.filesDir, IMAGE_DIR)

            // Create the directory if it doesn't exist
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }

            val imageFile = File(storageDir, imageFileName)

            context.contentResolver.openInputStream(imageUri)?.use { input ->
                FileOutputStream(imageFile).use { output ->
                    input.copyTo(output, 8192)
                }
            }

            imageFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image: ${e.message}", e)
            null
        }
    }

    /**
     * Gets a file path from a URI, handling both content and file URIs
     */
    fun getFilePathFromUri(context: Context, uri: String): String? {
        return try {
            val parsedUri = Uri.parse(uri)

            when (parsedUri.scheme?.lowercase()) {
                "content" -> {
                    // For content URIs, try to get the file path
                    context.contentResolver.query(parsedUri, arrayOf(android.provider.MediaStore.Images.Media.DATA), null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val columnIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA)
                            cursor.getString(columnIndex)
                        } else {
                            null
                        }
                    }
                }
                "file" -> parsedUri.path
                else -> uri // Assume it's already a file path
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting file path from URI: ${e.message}", e)
            null
        }
    }

    /**
     * Checks if an image file exists at the given path
     */
    fun isImageFileExists(filePath: String?): Boolean {
        if (filePath.isNullOrEmpty()) return false
        return try {
            val file = File(filePath)
            file.exists() && file.length() > 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Cleans up any invalid image references in the recipes
     * @param context The application context
     * @param imageUris List of valid image URIs that should be kept
     */
    fun cleanupOrphanedImages(context: Context, imageUris: List<String?>) {
        try {
            val storageDir = File(context.filesDir, IMAGE_DIR)
            if (!storageDir.exists()) return

            val validImagePaths = imageUris
                .filterNotNull()
                .map { getFilePathFromUri(context, it) }
                .filter { it != null }
                .toSet()

            storageDir.listFiles()?.forEach { file ->
                if (file.absolutePath !in validImagePaths) {
                    try {
                        file.delete()
                        Log.d(TAG, "Deleted orphaned image: ${file.absolutePath}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error deleting orphaned image: ${file.absolutePath}", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up orphaned images: ${e.message}", e)
        }
    }
}
