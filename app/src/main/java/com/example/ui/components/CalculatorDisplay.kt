package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorDisplay(
    expression: String,
    result: String,
    cursorPosition: Int = -1,
    onCursorPositionChange: (Int) -> Unit = {},
    isError: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 530),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "cursor",
    )

    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
    ) {
        // Expression line — tappable to place cursor
        val cursor = if (cursorPosition < 0 || cursorPosition > expression.length)
            expression.length else cursorPosition

        val annotatedExpr = buildAnnotatedString {
            if (expression.isEmpty()) {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))) {
                    append("|")
                }
            } else {
                append(expression.substring(0, cursor))
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary.copy(alpha = cursorAlpha))) {
                    append("|")
                }
                append(expression.substring(cursor))
            }
        }

        ClickableText(
            text = annotatedExpr,
            onClick = { offset ->
                textLayoutResult?.let { layout ->
                    // offset is the character index in the AnnotatedString
                    // Our annotated string has 1 extra char (the | cursor) inserted
                    // So offset maps to: offset if offset <= cursor, otherwise offset-1
                    val exprOffset = if (offset <= cursor) offset else offset - 1
                    onCursorPositionChange(exprOffset.coerceIn(0, expression.length))
                }
            },
            onTextLayout = { textLayoutResult = it },
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
                textAlign = TextAlign.End,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = when {
                isError -> "Error"
                result.isNotEmpty() -> result
                else -> "0"
            },
            fontSize = if (isError) 32.sp else if (result.length > 12) 48.sp else 56.sp,
            fontWeight = FontWeight.Light,
            color = when {
                isError -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
        )
    }
}
