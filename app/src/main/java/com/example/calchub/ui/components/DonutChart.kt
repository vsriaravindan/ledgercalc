package com.example.calchub.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.calchub.ui.theme.NeonText

/**
 * Data class representing a segment in the Donut Chart.
 *
 * @property value The numeric value of the segment.
 * @property color The color of the segment.
 * @property label The label for the segment.
 */
data class DonutChartData(
    val value: Double,
    val color: Color,
    val label: String
)

private fun formatTotal(total: Double): String {
    return if (total % 1.0 == 0.0) {
        total.toLong().toString()
    } else {
        String.format("%.2f", total)
    }
}

/**
 * An animated Donut Chart component using Canvas.
 * Displays data segments as arcs with a glow effect.
 *
 * @param data List of [DonutChartData] to display.
 * @param modifier Modifier for styling.
 * @param chartSize The size of the chart (width/height).
 * @param strokeWidth The thickness of the chart arcs.
 */
@Composable
fun DonutChart(
    data: List<DonutChartData>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 200.dp,
    strokeWidth: Dp = 30.dp
) {
    val total = data.sumOf { it.value }
    val proportions = data.map { it.value / total }
    val sweepAngles = proportions.map { it * 360f }

    val animation = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(chartSize),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                var startAngle = -90f
                val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                
                sweepAngles.forEachIndexed { index, sweepAngle ->
                    val strokeWidthPx = strokeWidth.toPx()
                    val glowWidthPx = strokeWidthPx + 20f // Increased glow width
                    
                    // Draw Glow
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            color = data[index].color.copy(alpha = 0.6f) // Increased opacity
                            style = PaintingStyle.Stroke
                            this.strokeWidth = glowWidthPx
                            strokeCap = StrokeCap.Round
                        }
                        paint.asFrameworkPaint().apply {
                            maskFilter = android.graphics.BlurMaskFilter(40f, android.graphics.BlurMaskFilter.Blur.NORMAL) // Increased blur
                        }
                        canvas.drawArc(
                            rect = androidx.compose.ui.geometry.Rect(
                                offset = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                                size = Size(size.width - strokeWidthPx, size.height - strokeWidthPx)
                            ),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle.toFloat() * animation.value,
                            useCenter = false,
                            paint = paint
                        )
                    }

                    // Draw Main Arc
                    drawArc(
                        color = data[index].color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle.toFloat() * animation.value,
                        useCenter = false,
                        style = stroke,
                        size = Size(size.width - strokeWidth.toPx(), size.height - strokeWidth.toPx()),
                        topLeft = Offset(strokeWidth.toPx() / 2, strokeWidth.toPx() / 2)
                    )
                    startAngle += sweepAngle.toFloat()
                }
            }
            
            // Center Text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonText.copy(alpha = 0.7f),
                )
                Text(
                    text = formatTotal(total),
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonText,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.size(16.dp))
        
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(12.dp)) {
                        drawCircle(color = item.color)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = item.label, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
