package com.leo.paleorecipes.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A utility class for handling file operations.
 */
@Singleton
class FileUtils @Inject constructor(private val context: Context) {

    private val authority: String by lazy { "${context.packageName}.fileprovider" }

    companion object {
        private const val IMAGE_DIRECTORY = "PaleoRecipes"
        private const val TEMP_FILE_PREFIX = "TEMP_"
        private const val TEMP_FILE_SUFFIX = ".tmp"

        @JvmStatic
        fun getMimeType(file: File): String {
            val name = file.name
            val lastDot = name.lastIndexOf('.').takeIf { it > 0 } ?: return "*/*"
            val ext = name.substring(lastDot + 1).lowercase(Locale.US)
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ?: "*/*"
        }
    }

    /**
     * Creates a temporary file in the cache directory.
     *
     * @param prefix The prefix of the file name.
     * @param suffix The suffix of the file name.
     * @return The created temporary file.
     */
    fun createTempFile(prefix: String = TEMP_FILE_PREFIX, suffix: String = TEMP_FILE_SUFFIX): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${prefix}${timeStamp}$suffix"
        val storageDir = context.externalCacheDir ?: context.cacheDir
        return File(storageDir, fileName)
    }

    /**
     * Creates an image file in the app's pictures directory.
     *
     * @param prefix The prefix of the file name.
     * @return The created image file.
     */
    fun createImageFile(prefix: String = "JPEG_"): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir

        // Create the directory if it doesn't exist
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        return File.createTempFile(
            "${prefix}${timeStamp}_",
            ".jpg",
            storageDir,
        )
    }

    /**
     * Gets the extension of a file name.
     *
     * @param fileName The file name.
     * @return The file extension (without the dot), or an empty string if there is no extension.
     */
    fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "")
    }

    /**
     * Gets a content URI for a file using FileProvider.
     *
     * @param file The file to get a URI for.
     * @return The content URI.
     */
    fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(context, authority, file)
    }

    /**
     * Gets the real path from a content URI.
     *
     * @param uri The content URI.
     * @return The real file path, or null if it cannot be determined.
     */
    fun getRealPathFromUri(uri: Uri): String? {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return "${Environment.getExternalStorageDirectory()}/${split[1]}"
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    id.toLong(),
                )
                return getDataColumn(context, contentUri, null, null)
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]

                val contentUri = when (type) {
                    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> null
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return contentUri?.let {
                    getDataColumn(context, it, selection, selectionArgs)
                }
            }
        }
        // MediaStore (and general)
        else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        }
        // File
        else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }

        return null
    }

    /**
     * Gets the display name of a file from its URI.
     *
     * @param uri The content URI.
     * @return The display name of the file, or null if it cannot be determined.
     */
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }

    /**
     * Copies a file from a source URI to a destination file.
     *
     * @param sourceUri The source URI.
     * @param destFile The destination file.
     * @return `true` if the copy was successful, `false` otherwise.
     */
    fun copyFile(sourceUri: Uri, destFile: File): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri)
            if (inputStream == null) {
                return false
            }
            inputStream.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Deletes a file or directory and all its contents.
     *
     * @param fileOrDirectory The file or directory to delete.
     * @return `true` if the file or directory was deleted, `false` otherwise.
     */
    fun deleteRecursive(fileOrDirectory: File): Boolean {
        if (fileOrDirectory.isDirectory) {
            val children = fileOrDirectory.listFiles()
            if (children != null) {
                for (child in children) {
                    deleteRecursive(child)
                }
            }
        }
        return fileOrDirectory.delete()
    }

    /**
     * Gets the size of a directory in bytes.
     *
     * @param directory The directory to get the size of.
     * @return The size of the directory in bytes.
     */
    fun getDirectorySize(directory: File): Long {
        var size: Long = 0
        if (directory.isDirectory) {
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    size += if (file.isDirectory) {
                        getDirectorySize(file)
                    } else {
                        file.length()
                    }
                }
            }
        } else if (directory.isFile) {
            size = directory.length()
        }
        return size
    }

    /**
     * Formats a file size in bytes to a human-readable string.
     *
     * @param size The size in bytes.
     * @return A human-readable string representing the size.
     */
    fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format(
            "%.1f %s",
            size / Math.pow(1024.0, digitGroups.toDouble()),
            units[digitGroups.coerceAtMost(units.size - 1)],
        )
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }
}
