package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun AppBackground(content: @Composable BoxScope.() -> Unit) {
    val bg = MaterialTheme.colorScheme.background
    val isDark = bg != Color.White // heuristic — dark bg means dark mode

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        if (isDark) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF7C3AED).copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(w * 0.1f, h * 0.15f),
                    ),
                    radius = w * 0.7f,
                    center = Offset(w * 0.1f, h * 0.15f),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF14B8A6).copy(alpha = 0.07f), Color.Transparent),
                        center = Offset(w * 0.9f, h * 0.85f),
                    ),
                    radius = w * 0.6f,
                    center = Offset(w * 0.9f, h * 0.85f),
                )
            }
        }
        content()
    }
}
