package com.syncpeer.syncpeerapp.themes

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.syncpeer.syncpeerapp.R

@Composable
fun SyncPeerAppTheme(content: @Composable () -> Unit) {
    val syncPeerAppColorScheme = ColorScheme(
        primary = SyncPeerAppColorScheme.primary,
        onPrimary = SyncPeerAppColorScheme.onPrimary,
        primaryContainer = SyncPeerAppColorScheme.primary,
        onPrimaryContainer = SyncPeerAppColorScheme.onPrimary,
        inversePrimary = SyncPeerAppColorScheme.primary,
        secondary = SyncPeerAppColorScheme.secondary,
        onSecondary = SyncPeerAppColorScheme.onSecondary,
        secondaryContainer = SyncPeerAppColorScheme.secondary,
        onSecondaryContainer = SyncPeerAppColorScheme.onSecondary,
        tertiary = Color.Black, // Placeholder, adjust as needed
        onTertiary = Color.White, // Placeholder, adjust as needed
        tertiaryContainer = Color.Black, // Placeholder, adjust as needed
        onTertiaryContainer = Color.White, // Placeholder, adjust as needed
        background = SyncPeerAppColorScheme.background,
        onBackground = SyncPeerAppColorScheme.onBackground,
        surface = SyncPeerAppColorScheme.surface,
        onSurface = SyncPeerAppColorScheme.onSurface,
        surfaceVariant = Color.Black, // Placeholder, adjust as needed
        onSurfaceVariant = Color.White, // Placeholder, adjust as needed
        surfaceTint = SyncPeerAppColorScheme.surface,
        inverseSurface = SyncPeerAppColorScheme.primary,
        inverseOnSurface = SyncPeerAppColorScheme.onPrimary,
        error = SyncPeerAppColorScheme.error,
        onError = SyncPeerAppColorScheme.onError,
        errorContainer = SyncPeerAppColorScheme.error,
        onErrorContainer = SyncPeerAppColorScheme.onError,
        outline = Color.Black, // Placeholder, adjust as needed
        outlineVariant = Color.Black, // Placeholder, adjust as needed
        scrim = Color.Black // Placeholder, adjust as needed
    )

    MaterialTheme(
        colorScheme = syncPeerAppColorScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}


object SyncPeerAppColorScheme {
     val primary = Color(R.color.purple_200) // Corresponds to @color/purple_200
     val primaryVariant = Color(R.color.purple_700) // Corresponds to @color/purple_700
     val secondary = Color.White // Corresponds to @color/white
     val secondaryVariant = Color(R.color.deep_blue) // Corresponds to @color/teal_200
     val background = Color(R.color.deep_blue) // Corresponds to @color/deep_blue
     val surface = Color(R.color.transparent) // Corresponds to @color/deep_blue
     val error = Color(0xFFA3001E)
     val onPrimary = Color.Black // Corresponds to @color/black
     val onSecondary = Color.Black // Corresponds to @color/black
     val onBackground = Color.White // Corresponds to @color/white
     val onSurface = Color.White // Corresponds to @color/white
     val onError = Color.White
     val isLight = false // Define whether the theme is light or dark
}
