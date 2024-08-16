package com.example.sneakerhub.Helpers

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class NetworkHelper(private val context: Context) {

    companion object {
        private const val TAG = "NetworkHelper"
        private const val TEST_URL = "https://www.google.com"
    }

    /**
     * Checks if the device is connected to the internet.
     * @return True if the device is connected to the internet, false otherwise.
     */
    suspend fun checkForInternet(): Boolean {
        return withContext(Dispatchers.IO) {
            if (isNetworkAvailable()) {
                isInternetAvailable()
            } else {
                false
            }
        }
    }

    /**
     * Checks if there is any active network connection.
     * @return True if a network is available, false otherwise.
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            networkInfo?.isConnected == true
        }
    }

    /**
     * Checks if the device can reach the internet.
     * @return True if the internet is reachable, false otherwise.
     */
    private fun isInternetAvailable(): Boolean {
        return try {
            val url = URL(TEST_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 1500
            connection.readTimeout = 1500
            connection.connect()
            val responseCode = connection.responseCode
            connection.disconnect()
            responseCode == HttpURLConnection.HTTP_OK
        } catch (e: IOException) {
            Log.e(TAG, "Internet check failed", e)
            false
        }
    }
}
