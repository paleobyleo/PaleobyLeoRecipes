package com.leo.paleorecipes

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * A custom test runner that enables Hilt to work with Android instrumentation tests.
 *
 * This is required for Hilt to work with the Android test environment.
 */
class HiltTestRunner : AndroidJUnitRunner() {

    @Throws(
        ClassNotFoundException::class,
        IllegalAccessException::class,
        InstantiationException::class,
    )
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?,
    ): Application {
        return super.newApplication(
            cl,
            HiltTestApplication::class.java.name,
            context,
        )
    }

    override fun onCreate(arguments: Bundle) {
        super.onCreate(arguments)
        // Add any test-specific initialization here
    }
}
