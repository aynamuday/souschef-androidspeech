package com.samsantech.souschef.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkHelper() {
    interface NetworkChangeListener {
        fun onNetworkChanged(isNetworkAvailable: Boolean)
    }
    companion object {
        object NetworkUtils {
            fun isNetworkAvailable(context: Context): Boolean {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val network = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

                return when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
        }
    }
    fun networkChangeReceiver(listener: NetworkChangeListener) = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                context?.let {
                    val isNetworkAvailable = NetworkUtils.isNetworkAvailable(it)
                    listener.onNetworkChanged(isNetworkAvailable)
                }
            }
        }
    }
}