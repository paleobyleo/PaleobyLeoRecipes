package com.leo.paleorecipes.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkUtils {

    /**
     * Check if there is an active internet connection available
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // For Android 10 (API 29) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                else -> false
            }
        } else {
            // For older versions
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    /**
     * Check if the device is connected to a metered network
     */
    fun isMeteredNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.isActiveNetworkMetered
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            when (networkInfo?.type) {
                ConnectivityManager.TYPE_MOBILE -> true
                ConnectivityManager.TYPE_WIFI -> false
                else -> true // Assume metered for other types to be safe
            }
        }
    }
}
