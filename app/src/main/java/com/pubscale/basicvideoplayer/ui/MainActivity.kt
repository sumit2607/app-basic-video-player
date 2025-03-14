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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.ui.PlayerView
import com.pubscale.basicvideoplayer.R
import com.pubscale.basicvideoplayer.repo.VideoRepository
import com.pubscale.basicvideoplayer.viewmodel.VideoViewModel
import com.pubscale.basicvideoplayer.viewmodel.VideoViewModelFactory
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var viewModel: VideoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)

        val repository = VideoRepository()
        val factory = VideoViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(VideoViewModel::class.java)

        viewModel.videoUrl.observe(this) { videoUrl ->
            setupExoPlayer(videoUrl)
        }

        viewModel.fetchVideoUrl()
    }

    private fun setupExoPlayer(videoUrl: String) {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(videoUrl))
            .setMediaMetadata(getVideoMetadata(videoUrl)) // Pass videoUrl here
            .build()

        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }


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

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }




    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val aspectRatio = Rational(playerView.width, playerView.height)
            val pipParams = PictureInPictureParams.Builder()
                .setAspectRatio(aspectRatio)
                .build()
            enterPictureInPictureMode(pipParams)
        }
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPictureInPictureMode(PictureInPictureParams.Builder().build())
        } else {
            super.onBackPressed()
        }
    }

    override fun onStop() {
        super.onStop()
        player?.playWhenReady = true  // Keeps playback active in background
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
