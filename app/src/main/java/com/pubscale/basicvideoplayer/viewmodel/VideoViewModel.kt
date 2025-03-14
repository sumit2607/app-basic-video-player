package com.pubscale.basicvideoplayer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class VideoViewModel : ViewModel() {
    private val _videoUrl = MutableLiveData<String>()
    val videoUrl: LiveData<String> get() = _videoUrl

    fun fetchVideoUrl() {
        viewModelScope.launch(Dispatchers.IO) {  // Runs the network request in the background
            try {
                val url =
                    URL("https://raw.githubusercontent.com/greedyraagava/test/refs/heads/main/video_url.json")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val inputStream = connection.inputStream
                val jsonString = inputStream.bufferedReader().use { it.readText() }

                val jsonObject = JSONObject(jsonString)
                val videoUrl = jsonObject.getString("url")

                _videoUrl.postValue(videoUrl)  // Updating LiveData on the main thread
            } catch (e: Exception) {
                Log.e("VideoViewModel", "Error fetching video URL", e)
            }
        }
    }

}
