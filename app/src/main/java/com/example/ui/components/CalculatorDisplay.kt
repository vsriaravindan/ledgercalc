package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorDisplay(
    expression: String,
    result: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = expression,
            fontSize = 24.sp,
            color = Color.White.copy(alpha = 0.5f),
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isError) "Error" else result.ifEmpty { "0" },
            fontSize = if (isError) 32.sp else 64.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = (-2).sp,
            color = if (isError) Color(0xFFEF4444) else Color.White,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )
    }
}
