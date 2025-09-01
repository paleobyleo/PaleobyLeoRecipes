package com.leo.paleorecipes.utils

import org.junit.Assert.*
import org.junit.Test

class AdMobUtilsTest {

    @Test
    fun `test AdMobUtils initialization flag`() {
        // This test verifies that the AdMobUtils class can be loaded
        // Note: We can't actually initialize AdMob in unit tests as it requires Android context
        assertFalse("isInitialized should default to false", AdMobUtils.isInitialized())
    }
}