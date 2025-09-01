package com.leo.paleorecipes

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        // ... existing code ...
        super.attachBaseContext(newBase)
        // ... existing code ...
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply dark mode setting
        // The DayNight theme handles dark mode automatically, so we always set the base theme.
        setTheme(R.style.Theme_PaleoRecipes)
    }
}