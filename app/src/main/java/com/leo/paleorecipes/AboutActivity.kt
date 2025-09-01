package com.leo.paleorecipes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // You will need to create an XML layout file for this activity, e.g., activity_about.xml
        // setContentView(R.layout.activity_about)
        // For now, we'll just set a simple title
        title = "About Paleo"
    }
}
