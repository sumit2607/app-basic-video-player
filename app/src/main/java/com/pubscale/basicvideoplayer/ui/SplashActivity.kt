package com.pubscale.basicvideoplayer.ui

import android.annotation.SuppressLint
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

/**
 * SplashActivity is the launch screen of the application.
 * It displays a splash screen for 2 seconds before navigating to MainActivity.
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    /**
     * Called when the activity is first created.
     * It sets up the splash screen and delays navigation to MainActivity.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the most recent data. Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables edge-to-edge UI mode for immersive display.
        setContentView(R.layout.activity_splash) // Sets the layout for the splash screen.

        // Launches a coroutine within the lifecycleScope to handle the delay.
        lifecycleScope.launch {
            delay(3000) // Waits for 2 seconds before proceeding.
            // Creates an intent to start MainActivity.
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent) // Starts MainActivity.
            this@SplashActivity.finish() // Finishes the SplashActivity to remove it from the back stack.
        }
    }
}
