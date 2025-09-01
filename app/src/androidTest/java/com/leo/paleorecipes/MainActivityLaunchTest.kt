package com.leo.paleorecipes

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityLaunchTest {

    @Test
    fun testMainActivityLaunches() {
        // Launch the MainActivityCompose
        ActivityScenario.launch(MainActivityCompose::class.java)
    }
}
