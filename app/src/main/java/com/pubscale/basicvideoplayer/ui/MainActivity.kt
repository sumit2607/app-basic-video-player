package com.pubscale.basicvideoplayer.ui

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.ui.PlayerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.pubscale.basicvideoplayer.R
import com.pubscale.basicvideoplayer.repo.VideoRepository
import com.pubscale.basicvideoplayer.viewmodel.VideoViewModel
import com.pubscale.basicvideoplayer.viewmodel.VideoViewModelFactory
import java.io.ByteArrayOutputStream

/**
 * MainActivity is responsible for playing a video using ExoPlayer.
 * It observes a video URL from the ViewModel, loads it into ExoPlayer,
 * and supports Picture-in-Picture (PiP) mode.
 */
class MainActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null  // ExoPlayer instance for video playback
    private var shimmerLayout: ShimmerFrameLayout? = null  // Shimmer effect while loading video
    private lateinit var playerView: PlayerView  // View to display the video player
    private lateinit var viewModel: VideoViewModel  // ViewModel to fetch video URL

    /**
     * Called when the activity is created.
     * Initializes UI components, sets up ViewModel, and starts video playback.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Initialize UI components
        playerView = findViewById(R.id.player_view)
        shimmerLayout = findViewById(R.id.shimmerLayout)

        // Initialize ViewModel with repository and factory
        val repository = VideoRepository(this)
        val factory = VideoViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[VideoViewModel::class.java]

        // Observe the video URL and start video playback
        viewModel.videoUrl.observe(this) { videoUrl ->
            shimmerLayout?.stopAndHide()
            playerView.visibility = View.VISIBLE
            setupExoPlayer(videoUrl)
        }

        // Fetch video URL from ViewModel
        viewModel.fetchVideoUrl()
    }

    /**
     * Sets up ExoPlayer with the given video URL.
     *
     * @param videoUrl The URL of the video to be played.
     */
    private fun setupExoPlayer(videoUrl: String) {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(videoUrl))
            .setMediaMetadata(getVideoMetadata(videoUrl)) // Attach metadata
            .build()

        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }

    /**
     * Extracts video metadata, such as a thumbnail frame, from the given video URL.
     *
     * @param videoUrl The URL of the video.
     * @return MediaMetadata containing title and artwork if available.
     */
    private fun getVideoMetadata(videoUrl: String): MediaMetadata {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoUrl, HashMap()) // Set video URL as data source
            val bitmap = retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST) // Get first frame

            if (bitmap != null) {
                return MediaMetadata.Builder()
                    .setTitle("My Video")
                    .setArtworkData(bitmapToByteArray(bitmap), MediaMetadata.PICTURE_TYPE_FRONT_COVER)
                    .build()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            retriever.release()
        }

        return MediaMetadata.Builder().setTitle("My Video").build()
    }

    /**
     * Converts a Bitmap into a ByteArray.
     *
     * @param bitmap The Bitmap to be converted.
     * @return A ByteArray representing the compressed bitmap.
     */
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    /**
     * Enters Picture-in-Picture (PiP) mode when the user minimizes the app.
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            playerView.post {
                val width = playerView.width
                val height = playerView.height

                if (width > 0 && height > 0) {
                    val aspectRatio = Rational(width, height)

                    val pipParams = PictureInPictureParams.Builder()
                        .setAspectRatio(aspectRatio)
                        .build()

                    // Ensure the player continues playing
                    if (!player!!.isPlaying) {
                        player!!.playWhenReady = true
                    }

                    enterPictureInPictureMode(pipParams)
                }
            }
        }
    }




    override fun onBackPressed() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            enterPictureInPictureMode(PictureInPictureParams.Builder().build())
//        } else {
//            super.onBackPressed()
//        }
        super.onBackPressed()
        finish()

    }

    /**
     * Ensures that playback continues in the background when the app is stopped.
     */
    override fun onStop() {
        super.onStop()
        player?.playWhenReady = true  // Keeps playback active in background
    }

    /**
     * Releases ExoPlayer resources when the activity is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    /**
     * Extension function to stop and hide the shimmer layout.
     */
    fun ShimmerFrameLayout?.stopAndHide() {
        this?.stopShimmer()
        this?.visibility = View.GONE
    }
}

