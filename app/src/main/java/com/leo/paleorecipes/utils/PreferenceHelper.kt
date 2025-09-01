package com.leo.paleorecipes.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A helper class for managing SharedPreferences with type-safe operations.
 */
@Singleton
class PreferenceHelper @Inject constructor(
    @ApplicationContext context: Context,
    private val preferenceName: String = "${context.packageName}_preferences",
) {
    private val prefs: Lazy<SharedPreferences> = lazy {
        context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
    }

    /**
     * Saves a value to SharedPreferences.
     *
     * @param key The key to store the value under.
     * @param value The value to store. Can be of type String, Int, Long, Float, Boolean, or Set<String>.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> save(key: String, value: T) {
        val editor = prefs.value.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Set<*> -> editor.putStringSet(key, value as Set<String>)
            null -> editor.remove(key)
            else -> throw UnsupportedOperationException("Unsupported type")
        }
        editor.apply()
    }

    /**
     * Retrieves a value from SharedPreferences.
     *
     * @param key The key of the value to retrieve.
     * @param defaultValue The default value to return if the key doesn't exist.
     * @return The value stored with the given key, or the default value if the key doesn't exist.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> prefs.value.getString(key, defaultValue) as T
            is Int -> prefs.value.getInt(key, defaultValue) as T
            is Long -> prefs.value.getLong(key, defaultValue) as T
            is Float -> prefs.value.getFloat(key, defaultValue) as T
            is Boolean -> prefs.value.getBoolean(key, defaultValue) as T
            is Set<*> -> prefs.value.getStringSet(key, defaultValue as Set<String>) as T
            else -> throw UnsupportedOperationException("Unsupported type")
        }
    }

    /**
     * Removes a value from SharedPreferences.
     *
     * @param key The key of the value to remove.
     */
    fun remove(key: String) {
        prefs.value.edit { remove(key) }
    }

    /**
     * Checks if a key exists in SharedPreferences.
     *
     * @param key The key to check.
     * @return `true` if the key exists, `false` otherwise.
     */
    fun contains(key: String): Boolean {
        return prefs.value.contains(key)
    }

    /**
     * Clears all values from SharedPreferences.
     */
    fun clear() {
        prefs.value.edit { clear() }
    }

    /**
     * Registers a callback to be invoked when a change happens to a preference.
     *
     * @param listener The callback that will run.
     * @return The SharedPreferences instance for method chaining.
     */
    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.value.registerOnSharedPreferenceChangeListener(listener)
    }

    /**
     * Unregisters a previous callback.
     *
     * @param listener The callback that should be unregistered.
     * @return The SharedPreferences instance for method chaining.
     */
    fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.value.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
