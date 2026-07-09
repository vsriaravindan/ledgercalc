package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Fill

@Composable
fun AppBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B)
                    )
                )
            )
    ) {
        // Mesh gradient effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF3B82F6).copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(width * 0.1f, height * 0.2f),
                    radius = width * 0.6f
                ),
                radius = width * 0.6f,
                center = Offset(width * 0.1f, height * 0.2f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF8B5CF6).copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(width * 0.9f, height * 0.8f),
                    radius = width * 0.6f
                ),
                radius = width * 0.6f,
                center = Offset(width * 0.9f, height * 0.8f)
            )
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF10B981).copy(alpha = 0.2f), Color.Transparent),
                    center = Offset(width * 0.8f, height * 0.1f),
                    radius = width * 0.5f
                ),
                radius = width * 0.5f,
                center = Offset(width * 0.8f, height * 0.1f)
            )
        }
        
        content()
    }
}
