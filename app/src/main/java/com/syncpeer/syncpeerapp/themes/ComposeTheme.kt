package com.syncpeer.syncpeerapp.themes

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.syncpeer.syncpeerapp.R

@Composable
fun SyncPeerAppTheme(context: Context,content: @Composable () -> Unit) {
    val colorSchema = ColorSchema(context)
    val syncPeerAppColorScheme = ColorScheme(
        primary = Color(colorSchema.primary),
        onPrimary = (colorSchema.onPrimary),
        primaryContainer = Color(colorSchema.primary),
        onPrimaryContainer = (colorSchema.onPrimary),
        inversePrimary = Color(colorSchema.primary),
        secondary = (colorSchema.secondary),
        onSecondary = (colorSchema.onSecondary),
        secondaryContainer = (colorSchema.secondary),
        onSecondaryContainer = (colorSchema.onSecondary),
        tertiary = Color.Black, // Placeholder, adjust as needed
        onTertiary = Color.White, // Placeholder, adjust as needed
        tertiaryContainer = Color.Black, // Placeholder, adjust as needed
        onTertiaryContainer = Color.White, // Placeholder, adjust as needed
        background = Color(colorSchema.background),
        onBackground = (colorSchema.onBackground),
        surface = Color(colorSchema.surface),
        onSurface = (colorSchema.onSurface),
        surfaceVariant = Color.Black, // Placeholder, adjust as needed
        onSurfaceVariant = Color.White, // Placeholder, adjust as needed
        surfaceTint = Color(colorSchema.surface),
        inverseSurface = Color(colorSchema.primary),
        inverseOnSurface = (colorSchema.onPrimary),
        error = (colorSchema.error),
        onError = (colorSchema.onError),
        errorContainer = (colorSchema.error),
        onErrorContainer = (colorSchema.onError),
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


