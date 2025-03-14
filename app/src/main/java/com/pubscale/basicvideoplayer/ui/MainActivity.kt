package com.pubscale.basicvideoplayer.ui

import android.app.PictureInPictureParams
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.pubscale.basicvideoplayer.R
import com.pubscale.basicvideoplayer.viewmodel.VideoViewModel


class MainActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var viewModel: VideoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)
        viewModel = ViewModelProvider(this).get(VideoViewModel::class.java)

        viewModel.videoUrl.observe(this) { videoUrl ->
            setupExoPlayer(videoUrl)
        }

        viewModel.fetchVideoUrl()
    }

    private fun setupExoPlayer(videoUrl: String) {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPictureInPictureMode(android.app.PictureInPictureParams.Builder().build())
        }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}

