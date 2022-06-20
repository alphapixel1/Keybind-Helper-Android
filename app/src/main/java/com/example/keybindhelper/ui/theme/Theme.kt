package com.example.keybindhelper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    /* set components to desired dark colors
    primary = ,
    primaryVariant = ,
    secondary =
    */
)

private val LightColorPalette = lightColors(
    /* set components to desired light colors
    primary = ,
    primaryVariant = ,
    secondary =
    */
)

@Composable
fun MyComposeApplicationTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}
