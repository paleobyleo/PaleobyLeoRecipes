package com.leo.paleorecipes

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log // Ensure this import is present
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.leo.paleorecipes.R
import com.leo.paleorecipes.MainActivity

class SplashActivity : AppCompatActivity() {
    private val TAG = "SplashActivity" // Ensure this line is present and correct

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            Log.d(TAG, "onCreate: Starting SplashActivity")
            super.onCreate(savedInstanceState)

            // Remove the WindowCompat line that was causing issues
            // WindowCompat.setDecorFitsSystemWindows(window, false)

            setContentView(R.layout.activity_splash)

            Log.d(TAG, "onCreate: SplashActivity layout set")

            // Delay for 3 seconds then start MainActivity
            Handler(Looper.getMainLooper()).postDelayed( {
                try {
                    Log.d(TAG, "Handler: Preparing to start MainActivity")

                    // Create the intent
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)

                    // Add flags to clear the stack
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    Log.d(TAG, "Handler: Starting MainActivity")
                    startActivity(intent)

                    // Show a toast to confirm the transition
                    Toast.makeText(this, "Starting main screen", Toast.LENGTH_SHORT).show()

                    // Finish this activity
                    finish()

                    Log.d(TAG, "Handler: SplashActivity finished")
                } catch (e: Exception) {
                    Log.e(TAG, "Handler: Error starting MainActivity", e)
                    Toast.makeText(this, "Error: " + e.message, Toast.LENGTH_LONG).show()
                }
            } , 3000)

        } catch (e: Exception) {
            Log.e(TAG, "onCreate: Fatal error in SplashActivity", e)
            Toast.makeText(this, "Splash error: " + e.message, Toast.LENGTH_LONG).show()
        }
    }
}