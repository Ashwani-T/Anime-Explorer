package com.example.animeexplorer.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject


interface ConnectivityObserver {
    fun observer(): Flow<Boolean>
}

class NetworkConnectivityObserver @Inject constructor(
    @ApplicationContext context: Context
) : ConnectivityObserver {


    private val connectivityManager = context.getSystemService<ConnectivityManager>()

    override fun observer(): Flow<Boolean> = callbackFlow {

        val callback = object : ConnectivityManager.NetworkCallback() {

            fun sendCurrentState() {
                val network = connectivityManager?.activeNetwork
                val caps = connectivityManager?.getNetworkCapabilities(network)

                val isConnected =
                    caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

                trySend(isConnected)
            }



            override fun onAvailable(network: Network) {
                sendCurrentState()
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                sendCurrentState()
            }

            override fun onLost(network: Network) {
                sendCurrentState()
            }

            override fun onUnavailable() {
                trySend(false)
            }
        }
        callback.sendCurrentState()

        connectivityManager?.registerDefaultNetworkCallback(callback)

        awaitClose {
            connectivityManager?.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

}