package com.example.companio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Blue500,
    primaryVariant = Blue200,
    secondary = Color.Gray,
    error = Color.Red,
    background = Color.White
)

private val LightColorPalette = lightColors(
    primary = Blue500,
    primaryVariant = Blue200,
    secondary = Color.Gray,
    error = Color.Red,
    background = Color.White

)

@Composable
fun ObjectDetectionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}