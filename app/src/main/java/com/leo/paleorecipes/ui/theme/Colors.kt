package com.leo.paleorecipes.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Get the appropriate color scheme based on the current theme mode.
 *
 * @param darkTheme Whether the dark theme is enabled
 * @return The appropriate color scheme
 */
fun getColorScheme(darkTheme: Boolean): ColorScheme {
    return if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFFFB59E), // Light Orange
            onPrimary = Color(0xFF5D1900),
            primaryContainer = Color(0xFF7D2C0E),
            onPrimaryContainer = Color(0xFFFFDBCF),

            secondary = Color(0xFFE7BDB0), // Light Brown
            onSecondary = Color(0xFF442A1E),
            secondaryContainer = Color(0xFF5D3F33),
            onSecondaryContainer = Color(0xFFFFDBCF),

            tertiary = Color(0xFFD7C48C), // Light Olive
            onTertiary = Color(0xFF3D2F04),
            tertiaryContainer = Color(0xFF524519),
            onTertiaryContainer = Color(0xFFF4E0A6),

            background = Color(0xFF201A18), // Dark Brown
            onBackground = Color(0xFFEDE0DD),

            surface = Color(0xFF201A18), // Same as background
            onSurface = Color(0xFFEDE0DD),

            surfaceVariant = Color(0xFF53433F), // Dark Orange
            onSurfaceVariant = Color(0xFFD8C2BC),

            error = Color(0xFFFFB4AB), // Light Red
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),

            outline = Color(0xFFA08C87), // Light Gray
            outlineVariant = Color(0xFF53433F),
            scrim = Color(0xFF000000),
            inverseSurface = Color(0xFFEDE0DD),
            inverseOnSurface = Color(0xFF362F2D),
            inversePrimary = Color(0xFF9C4221),
            surfaceTint = Color(0xFFFFB59E),
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF9C4221), // Deep Orange
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFDBCF),
            onPrimaryContainer = Color(0xFF3B0900),

            secondary = Color(0xFF77574A), // Brown
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFFDBCF),
            onSecondaryContainer = Color(0xFF2C160B),

            tertiary = Color(0xFF6B5D2F), // Olive
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFF4E0A6),
            onTertiaryContainer = Color(0xFF231B00),

            background = Color(0xFFFFF8F6), // Off-white
            onBackground = Color(0xFF201A18),

            surface = Color(0xFFFFF8F6), // Same as background
            onSurface = Color(0xFF201A18),

            surfaceVariant = Color(0xFFF5DDD7), // Light orange
            onSurfaceVariant = Color(0xFF53433F),

            error = Color(0xFFBA1A1A), // Red
            onError = Color.White,
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),

            outline = Color(0xFF85736E), // Gray
            outlineVariant = Color(0xFFD8C2BC),
            scrim = Color(0xFF000000),
            inverseSurface = Color(0xFF362F2D),
            inverseOnSurface = Color(0xFFFBEEEA),
            inversePrimary = Color(0xFFFFB59E),
            surfaceTint = Color(0xFF9C4221),
        )
    }
}
