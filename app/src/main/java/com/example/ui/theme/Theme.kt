package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class QuranReadingTheme {
  LIGHT,
  SEPIA,
  DARK
}

val QuranDarkColorScheme =
  darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF03331E),
    primaryContainer = Color(0xFF1B4D36),
    onPrimaryContainer = Color(0xFFB8F2D5),
    secondary = DarkGold,
    onSecondary = Color(0xFF3F2E00),
    secondaryContainer = Color(0xFF594300),
    onSecondaryContainer = Color(0xFFFFE088),
    tertiary = Color(0xFF8AD4BE),
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF2C443A)
  )

val QuranLightColorScheme =
  lightColorScheme(
    primary = IslamicGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = IslamicGreenContainer,
    onPrimaryContainer = IslamicOnGreenContainer,
    secondary = IslamicGoldDark,
    onSecondary = Color.White,
    secondaryContainer = IslamicGoldContainer,
    onSecondaryContainer = Color(0xFF4C3800),
    tertiary = IslamicGreenLight,
    background = CreamWhite,
    onBackground = NeutralText,
    surface = Color.White,
    onSurface = NeutralText,
    surfaceVariant = Color(0xFFF1F6F3),
    onSurfaceVariant = NeutralMuted,
    outline = BorderLight
  )

val QuranSepiaColorScheme =
  lightColorScheme(
    primary = Color(0xFF1F5C3E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE9E0CF),
    onPrimaryContainer = Color(0xFF2C2213),
    secondary = Color(0xFF9A7B2C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF5EEDB),
    onSecondaryContainer = Color(0xFF443306),
    tertiary = Color(0xFF38684F),
    background = ParchmentBackground,
    onBackground = Color(0xFF2A2318),
    surface = ParchmentSurface,
    onSurface = Color(0xFF2A2318),
    surfaceVariant = Color(0xFFF0E8D7),
    onSurfaceVariant = Color(0xFF6B5F4D),
    outline = ParchmentCardBorder
  )

@Composable
fun QuranAppTheme(
  readingTheme: QuranReadingTheme = QuranReadingTheme.LIGHT,
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when (readingTheme) {
      QuranReadingTheme.DARK -> QuranDarkColorScheme
      QuranReadingTheme.SEPIA -> QuranSepiaColorScheme
      QuranReadingTheme.LIGHT -> if (darkTheme) QuranDarkColorScheme else QuranLightColorScheme
    }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
