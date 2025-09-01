package com.leo.paleorecipes.ui.components

import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.leo.paleorecipes.BuildConfig

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = if (BuildConfig.DEBUG) {
        "ca-app-pub-3940256099942544/6300978111" // Test ad unit ID
    } else {
        "ca-app-pub-3031011439814812/6300978111" // Using your provided AdMob ID with standard banner suffix
    }
) {
    val context = LocalContext.current
    val adView = remember { AdView(context) }

    DisposableEffect(adView) {
        onDispose {
            adView.destroy()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        AndroidView(
            factory = { ctx ->
                adView.apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    val adRequest = AdRequest.Builder().build()
                    loadAd(adRequest)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.Transparent)
        )
    }
}