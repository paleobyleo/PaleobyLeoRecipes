package com.leo.paleorecipes.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A class that observes network connectivity status and provides it as a Flow.
 */
@Singleton
class NetworkConnectivityObserver @Inject constructor(
    @ApplicationContext context: Context,
) : NetworkMonitor {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val isOnline: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            private val networks = mutableSetOf<Network>()

            override fun onAvailable(network: Network) {
                networks += network
                updateConnectionStatus()
            }

            override fun onLost(network: Network) {
                networks -= network
                updateConnectionStatus()
            }

            private fun updateConnectionStatus() {
                launch {
                    val isConnected = networks.any { network ->
                        connectivityManager.getNetworkCapabilities(network)
                            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            ?: false
                    }
                    send(isConnected)
                }
            }
        }

        // Register the callback
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Initial state
        launch {
            send(NetworkUtils.isNetworkAvailable(context))
        }

        // Unregister the callback when the flow is cancelled
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
        .flowOn(kotlinx.coroutines.Dispatchers.IO)

    /**
     * Check if the device is currently connected to the internet
     */
    fun isCurrentlyConnected(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected == true
        }
    }
}
