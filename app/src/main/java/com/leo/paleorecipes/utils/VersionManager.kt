package com.leo.paleorecipes.utils

import android.content.Context
import com.leo.paleorecipes.BuildConfig
import org.json.JSONObject
import timber.log.Timber
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility class for managing app version information
 */
class VersionManager(private val context: Context) {

    data class VersionInfo(
        val versionCode: Int,
        val versionName: String,
        val releaseDate: String,
        val changes: List<String>,
    )

    data class VersionHistory(
        val currentVersion: VersionInfo,
        val versionHistory: List<VersionInfo>,
    )

    /**
     * Get the current version information from the version history file
     */
    fun getCurrentVersionInfo(): VersionInfo? {
        return try {
            val versionHistory = getVersionHistory()
            versionHistory?.currentVersion
        } catch (e: Exception) {
            Timber.e(e, "Error getting current version info")
            null
        }
    }

    /**
     * Get the complete version history
     */
    fun getVersionHistory(): VersionHistory? {
        return try {
            val inputStream: InputStream = context.assets.open("version_history.json")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val json = String(buffer, Charsets.UTF_8)

            val jsonObject = JSONObject(json)
            val currentVersionObj = jsonObject.getJSONObject("currentVersion")
            val versionHistoryArray = jsonObject.getJSONArray("versionHistory")

            val currentVersion = parseVersionInfo(currentVersionObj)

            val versionHistory = mutableListOf<VersionInfo>()
            for (i in 0 until versionHistoryArray.length()) {
                val versionObj = versionHistoryArray.getJSONObject(i)
                versionHistory.add(parseVersionInfo(versionObj))
            }

            VersionHistory(currentVersion, versionHistory)
        } catch (e: Exception) {
            Timber.e(e, "Error reading version history")
            null
        }
    }

    /**
     * Check if a new version is available
     */
    fun isNewVersionAvailable(): Boolean {
        return try {
            val currentVersionInfo = getCurrentVersionInfo()
            currentVersionInfo?.versionCode ?: 0 < BuildConfig.VERSION_CODE
        } catch (e: Exception) {
            Timber.e(e, "Error checking for new version")
            false
        }
    }

    /**
     * Get formatted version string for display
     */
    fun getFormattedVersionString(): String {
        return try {
            "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        } catch (e: Exception) {
            "Version unknown"
        }
    }

    /**
     * Get the release date of the current version
     */
    fun getCurrentVersionReleaseDate(): String {
        return try {
            getCurrentVersionInfo()?.releaseDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        } catch (e: Exception) {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        }
    }

    /**
     * Parse a JSONObject into a VersionInfo object
     */
    private fun parseVersionInfo(jsonObject: JSONObject): VersionInfo {
        val versionCode = jsonObject.getInt("versionCode")
        val versionName = jsonObject.getString("versionName")
        val releaseDate = jsonObject.getString("releaseDate")

        val changesArray = jsonObject.getJSONArray("changes")
        val changes = mutableListOf<String>()
        for (i in 0 until changesArray.length()) {
            changes.add(changesArray.getString(i))
        }

        return VersionInfo(versionCode, versionName, releaseDate, changes)
    }

    companion object {
        const val TAG = "VersionManager"
    }
}
