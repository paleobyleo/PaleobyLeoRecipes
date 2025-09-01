package com.leo.paleorecipes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class FridgeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fridge)

        // Set up the toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Fridge"
    }
}
