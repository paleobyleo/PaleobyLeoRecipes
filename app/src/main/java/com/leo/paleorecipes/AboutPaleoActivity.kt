package com.leo.paleorecipes

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class AboutPaleoActivity : AppCompatActivity() {
    private val TAG = "AboutPaleoActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_paleo)

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