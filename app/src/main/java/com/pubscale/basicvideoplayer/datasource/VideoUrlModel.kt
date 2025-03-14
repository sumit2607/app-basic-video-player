package com.pubscale.basicvideoplayer.datasource

/**
 * Data model representing the structure of the video URL JSON response.
 *
 * @property url The direct URL of the video to be played.
 */
data class VideoUrlModel(
    val url: String  // Holds the video URL fetched from the JSON response
)

