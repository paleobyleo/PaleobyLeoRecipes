package com.leo.paleorecipes.utils

import android.content.Context
import com.leo.paleorecipes.R
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * A utility class to handle API errors and convert them to user-friendly messages.
 */
object ApiErrorHandler {

    /**
     * Handles API errors and returns a user-friendly error message.
     *
     * @param throwable The throwable to handle
     * @param context The application context for string resources
     * @return A user-friendly error message
     */
    fun getErrorMessage(throwable: Throwable, context: Context): String {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    400 -> context.getString(R.string.error_bad_request)
                    401 -> context.getString(R.string.error_unauthorized)
                    403 -> context.getString(R.string.error_forbidden)
                    404 -> context.getString(R.string.error_not_found)
                    408 -> context.getString(R.string.error_request_timeout)
                    409 -> context.getString(R.string.error_conflict)
                    429 -> context.getString(R.string.error_too_many_requests)
                    500 -> context.getString(R.string.error_server_error)
                    502 -> context.getString(R.string.error_bad_gateway)
                    503 -> context.getString(R.string.error_service_unavailable)
                    504 -> context.getString(R.string.error_gateway_timeout)
                    else -> context.getString(R.string.error_unknown_http, throwable.code())
                }
            }
            is SocketTimeoutException -> context.getString(R.string.error_connection_timeout)
            is UnknownHostException -> context.getString(R.string.error_no_internet_connection)
            is IOException -> context.getString(R.string.error_network_io)
            else -> context.getString(R.string.error_unknown, throwable.message ?: "")
        }
    }

    /**
     * Handles API errors and returns a user-friendly error message.
     * This is a suspend function that can be used in coroutines.
     *
     * @param throwable The throwable to handle
     * @param context The application context for string resources
     * @return A user-friendly error message
     */
    suspend fun getErrorMessageSuspend(throwable: Throwable, context: Context): String {
        return getErrorMessage(throwable, context)
    }
}
