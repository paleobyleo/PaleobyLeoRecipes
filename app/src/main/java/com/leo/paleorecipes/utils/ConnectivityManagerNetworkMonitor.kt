package com.leo.paleorecipes.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ConnectivityManagerNetworkMonitor(
    private val context: Context,
) : NetworkMonitor {

    override val isOnline: Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Unregister the callback when the flow is cancelled
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
}
