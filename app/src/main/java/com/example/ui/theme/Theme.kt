package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = EmeraldDarkPrimary,
  onPrimary = EmeraldOnContainer,
  primaryContainer = EmeraldDarkContainer,
  onPrimaryContainer = EmeraldContainer,
  secondary = GoldSecondary,
  onSecondary = GoldOnSecondary,
  secondaryContainer = GoldContainer,
  onSecondaryContainer = GoldOnContainer,
  background = DarkSurface,
  surface = DarkCardSurface,
  onBackground = DarkTextLight,
  onSurface = DarkTextLight,
)

private val LightColorScheme = lightColorScheme(
  primary = EmeraldPrimary,
  onPrimary = EmeraldOnPrimary,
  primaryContainer = EmeraldContainer,
  onPrimaryContainer = EmeraldOnContainer,
  secondary = GoldSecondary,
  onSecondary = GoldOnSecondary,
  secondaryContainer = GoldContainer,
  onSecondaryContainer = GoldOnContainer,
  background = SurfaceLight,
  surface = CardSurfaceLight,
  onBackground = TextDark,
  onSurface = TextDark,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent branding colors
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
