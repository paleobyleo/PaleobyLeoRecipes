package com.leo.paleorecipes

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.leo.paleorecipes.ui.about.AboutPaleoScreen
import com.leo.paleorecipes.ui.theme.PaleoRecipesTheme

class AboutPaleoActivity : AppCompatActivity() {
    private val TAG = "AboutPaleoActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set up the Compose UI
        setContent {
            // Set solid black background for the entire app
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            ) {
                PaleoRecipesTheme {
                    AboutPaleoScreen(
                        onNavigateBack = { finish() }
                    )
                }
            }
        }

        // Set up the action bar with back button
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "About Paleo Diet"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}