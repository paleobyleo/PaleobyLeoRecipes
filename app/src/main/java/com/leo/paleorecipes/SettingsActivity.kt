package com.leo.paleorecipes

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.leo.paleorecipes.utils.VersionManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        // Display version information
        displayVersionInfo()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayVersionInfo() {
        try {
            val versionManager = VersionManager(this)
            val versionTextView = findViewById<TextView>(R.id.text_view_version)
            versionTextView.text = versionManager.getFormattedVersionString()
        } catch (e: Exception) {
            // Fallback to default version string
            val versionTextView = findViewById<TextView>(R.id.text_view_version)
            versionTextView.text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        }
    }
}
