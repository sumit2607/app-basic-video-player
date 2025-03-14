package com.pubscale.basicvideoplayer.repo

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.pubscale.basicvideoplayer.datasource.VideoUrlModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class VideoRepository(private val context: Context) {

    private val videoUrlEndpoint =
        "https://raw.githubusercontent.com/greedyraagava/test/refs/heads/main/video_url.json"

    /**
     * Fetches the video URL from the remote JSON file.
     * Ensures execution on Dispatchers.IO and handles network errors.
     *
     * @return The video URL as a String, or null if an error occurs.
     */
    suspend fun fetchVideoUrl(): String? {
        return withContext(Dispatchers.IO) {
            if (!isInternetAvailable()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                }
                return@withContext null
            }

            try {
                val url = URL(videoUrlEndpoint)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val inputStream = connection.inputStream
                val jsonString = inputStream.bufferedReader().use { it.readText() }

                val videoUrlModel = Gson().fromJson(jsonString, VideoUrlModel::class.java)
                return@withContext videoUrlModel.url
            } catch (e: Exception) {
                Log.e("VideoRepository", "Error fetching video URL", e)
                return@withContext null
            }
        }
    }

    /**
     * Checks if the device has an active internet connection.
     *
     * @return true if connected to the internet, otherwise false.
     */
    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
