package com.pubscale.basicvideoplayer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pubscale.basicvideoplayer.repo.VideoRepository
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for providing the video URL.
 * It interacts with VideoRepository to fetch the data.
 */
class VideoViewModel(private val repository: VideoRepository) : ViewModel() {

    // LiveData to hold the fetched video URL
    private val _videoUrl = MutableLiveData<String>()
    val videoUrl: LiveData<String> get() = _videoUrl

    /**
     * Fetches the video URL from the repository and updates LiveData.
     * Runs in a coroutine to ensure background execution.
     */
    fun fetchVideoUrl() {
        viewModelScope.launch {
            val videoUrl = repository.fetchVideoUrl()
            videoUrl?.let {
                _videoUrl.postValue(it)  // Update LiveData with fetched URL
            }
        }
    }
}
