package com.pubscale.basicvideoplayer.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.pubscale.basicvideoplayer.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        lifecycleScope.launch {
            delay(2000).apply {
                val intent = Intent(this@SplashActivity , MainActivity::class.java)
                startActivity(intent)
                this@SplashActivity.finish()
            }
        }

    }
}