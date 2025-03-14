package com.pubscale.basicvideoplayer.repo

import android.util.Log
import com.google.gson.Gson
import com.pubscale.basicvideoplayer.datasource.VideoUrlModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Repository responsible for fetching the video URL from a remote JSON file.
 * It abstracts the data source, making the ViewModel cleaner and testable.
 */
class VideoRepository {

    private val videoUrlEndpoint = "https://raw.githubusercontent.com/greedyraagava/test/refs/heads/main/video_url.json"

    /**
     * Fetches the video URL from the remote JSON file.
     * This function runs in a background thread using Dispatchers.IO.
     *
     * @return The video URL as a String, or null if an error occurs.
     */
    suspend fun fetchVideoUrl(): String? {
        return withContext(Dispatchers.IO) {  // Runs in background thread
            try {
                // Create a URL object
                val url = URL(videoUrlEndpoint)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                // Read response stream and convert it to a string
                val inputStream = connection.inputStream
                val jsonString = inputStream.bufferedReader().use { it.readText() }

                // Parse JSON using Gson and map it to VideoUrlModel
                val videoUrlModel = Gson().fromJson(jsonString, VideoUrlModel::class.java)

                return@withContext videoUrlModel.url
            } catch (e: Exception) {
                Log.e("VideoRepository", "Error fetching video URL", e)
                return@withContext null
            }
        }
    }
}
