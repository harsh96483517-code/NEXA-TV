package com.nexatv.launcher.platform

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

enum class NetworkStatus { WIFI, ETHERNET, OTHER, OFFLINE }

/** Observes the default network using ACCESS_NETWORK_STATE (a normal, install-time permission). */
class NetworkMonitor(context: Context) {

    private val connectivity: ConnectivityManager? =
        context.getSystemService(ConnectivityManager::class.java)

    fun status(): Flow<NetworkStatus> = callbackFlow {
        val manager = connectivity
        if (manager == null) {
            trySend(NetworkStatus.OFFLINE)
            awaitClose { }
            return@callbackFlow
        }

        trySend(currentStatus(manager))

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                trySend(classify(capabilities))
            }

            override fun onLost(network: Network) {
                trySend(NetworkStatus.OFFLINE)
            }
        }

        val registered = runCatching { manager.registerDefaultNetworkCallback(callback) }.isSuccess
        awaitClose {
            if (registered) runCatching { manager.unregisterNetworkCallback(callback) }
        }
    }.distinctUntilChanged()

    private fun currentStatus(manager: ConnectivityManager): NetworkStatus = runCatching {
        val network = manager.activeNetwork ?: return@runCatching NetworkStatus.OFFLINE
        val capabilities = manager.getNetworkCapabilities(network)
            ?: return@runCatching NetworkStatus.OFFLINE
        classify(capabilities)
    }.getOrDefault(NetworkStatus.OFFLINE)

    private fun classify(capabilities: NetworkCapabilities): NetworkStatus = when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkStatus.WIFI
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkStatus.ETHERNET
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> NetworkStatus.OTHER
        else -> NetworkStatus.OFFLINE
    }
}
