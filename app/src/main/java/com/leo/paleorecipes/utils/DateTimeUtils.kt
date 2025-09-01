package com.leo.paleorecipes.utils

import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A utility class for handling date and time operations.
 */
@Singleton
class DateTimeUtils @Inject constructor() {

    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val TIME_FORMAT = "HH:mm"
        const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm"
        const val DISPLAY_DATE_FORMAT = "MMM d, yyyy"
        const val DISPLAY_TIME_FORMAT = "h:mm a"
        const val DISPLAY_DATE_TIME_FORMAT = "MMM d, yyyy h:mm a"
        const val ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }

    private val defaultLocale = Locale.getDefault()

    /**
     * Formats a [Date] object to a string using the specified format pattern.
     *
     * @param date The date to format.
     * @param pattern The pattern to use for formatting.
     * @return The formatted date string.
     */
    fun formatDate(date: Date, pattern: String = DATE_FORMAT): String {
        return SimpleDateFormat(pattern, defaultLocale).format(date)
    }

    /**
     * Parses a date string into a [Date] object using the specified format pattern.
     *
     * @param dateString The date string to parse.
     * @param pattern The pattern to use for parsing.
     * @return The parsed [Date] object, or null if parsing fails.
     */
    fun parseDate(dateString: String, pattern: String = DATE_FORMAT): Date? {
        return try {
            SimpleDateFormat(pattern, defaultLocale).parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Gets the current date and time as a [Date] object.
     *
     * @return The current date and time.
     */
    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    /**
     * Gets the current date and time as a formatted string.
     *
     * @param pattern The pattern to use for formatting.
     * @return The formatted current date and time string.
     */
    fun getCurrentDateTimeFormatted(pattern: String = DATE_TIME_FORMAT): String {
        return formatDate(getCurrentDateTime(), pattern)
    }

    /**
     * Adds the specified number of days to the given date.
     *
     * @param date The base date.
     * @param days The number of days to add (can be negative to subtract days).
     * @return A new [Date] object with the specified number of days added.
     */
    fun addDays(date: Date, days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return calendar.time
    }

    /**
     * Calculates the difference in days between two dates.
     *
     * @param startDate The start date.
     * @param endDate The end date.
     * @return The number of days between the two dates.
     */
    fun getDaysBetween(startDate: Date, endDate: Date): Long {
        val diffInMillis = endDate.time - startDate.time
        return diffInMillis / (24 * 60 * 60 * 1000)
    }

    /**
     * Converts a timestamp in milliseconds to a formatted date string.
     *
     * @param timestamp The timestamp in milliseconds.
     * @param pattern The pattern to use for formatting.
     * @return The formatted date string.
     */
    fun timestampToDateString(timestamp: Long, pattern: String = DATE_FORMAT): String {
        return formatDate(Date(timestamp), pattern)
    }

    /**
     * Converts a date string to a timestamp in milliseconds.
     *
     * @param dateString The date string to convert.
     * @param pattern The pattern of the input date string.
     * @return The timestamp in milliseconds, or -1 if parsing fails.
     */
    fun dateStringToTimestamp(dateString: String, pattern: String = DATE_FORMAT): Long {
        return parseDate(dateString, pattern)?.time ?: -1
    }

    /**
     * Checks if a date is today.
     *
     * @param date The date to check.
     * @return `true` if the date is today, `false` otherwise.
     */
    fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val targetDate = Calendar.getInstance()
        targetDate.time = date

        return today.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == targetDate.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Checks if a date is in the past.
     *
     * @param date The date to check.
     * @return `true` if the date is in the past, `false` otherwise.
     */
    fun isPastDate(date: Date): Boolean {
        return date.before(Calendar.getInstance().time)
    }

    /**
     * Gets a user-friendly relative time string (e.g., "2 hours ago", "3 days ago").
     *
     * @param date The date to format.
     * @return A user-friendly relative time string.
     */
    fun getRelativeTimeSpanString(date: Date): String {
        val now = System.currentTimeMillis()
        val diffInSeconds = (now - date.time) / 1000

        return when {
            diffInSeconds < 60 -> "Just now"
            diffInSeconds < 3600 -> "${diffInSeconds / 60}m ago"
            diffInSeconds < 86400 -> "${diffInSeconds / 3600}h ago"
            diffInSeconds < 604800 -> "${diffInSeconds / 86400}d ago"
            diffInSeconds < 2592000 -> "${diffInSeconds / 604800}w ago"
            diffInSeconds < 31536000 -> "${diffInSeconds / 2592000}mo ago"
            else -> "${diffInSeconds / 31536000}y ago"
        }
    }
}
