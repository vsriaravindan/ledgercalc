package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorPad(
    onInput: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onEvaluate: () -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Each row gets equal share of remaining vertical space
        Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcGlassButton(text = "C", modifier = Modifier.weight(1f).fillMaxHeight(), isClear = true, onClick = onClear)
            CalcGlassButton(text = "÷", modifier = Modifier.weight(1f).fillMaxHeight(), isOperator = true, onClick = { onInput("÷") })
            CalcGlassButton(text = "×", modifier = Modifier.weight(1f).fillMaxHeight(), isOperator = true, onClick = { onInput("×") })
            CalcGlassButton(text = "%", modifier = Modifier.weight(1f).fillMaxHeight(), isOperator = true, onClick = { onInput("%") })
            CalcGlassButton(text = "⌫", modifier = Modifier.weight(1f).fillMaxHeight(), isOperator = true, onClick = onDelete)
        }
        Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("7", "8", "9", "-").forEach { btn ->
                CalcGlassButton(text = btn, modifier = Modifier.weight(1f).fillMaxHeight(), isOperator = btn == "-", onClick = { onInput(btn) })
            }
        }
        Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("4", "5", "6", "+").forEach { btn ->
                CalcGlassButton(text = btn, modifier = Modifier.weight(1f).fillMaxHeight(), isOperator = btn == "+", onClick = { onInput(btn) })
            }
        }
        Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("1", "2", "3").forEach { btn ->
                CalcGlassButton(text = btn, modifier = Modifier.weight(1f).fillMaxHeight(), onClick = { onInput(btn) })
            }
            CalcGlassButton(text = "=", modifier = Modifier.weight(1f).fillMaxHeight(), isEquals = true, onClick = onEvaluate)
        }
        Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcGlassButton(text = "0", modifier = Modifier.weight(2f).fillMaxHeight(), onClick = { onInput("0") })
            CalcGlassButton(text = ".", modifier = Modifier.weight(1f).fillMaxHeight(), onClick = { onInput(".") })
            CalcGlassButton(text = "SAVE", modifier = Modifier.weight(1.5f).fillMaxHeight(), isPrimary = true, onClick = onEquals)
        }
    }
}

@Composable
fun ScientificPad(
    onInput: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf(
            listOf("(", ")", "^", "√", "π"),
            listOf("sin", "cos", "tan", "log", "ln"),
        ).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth().height(38.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { btn ->
                    CalcGlassButton(text = btn, modifier = Modifier.weight(1f).fillMaxHeight(), isOperator = true, fontSize = 13.sp, onClick = { onInput(btn) })
                }
            }
        }
    }
}

@Composable
fun CalcGlassButton(
    text: String,
    modifier: Modifier = Modifier,
    isOperator: Boolean = false,
    isClear: Boolean = false,
    isEquals: Boolean = false,
    isPrimary: Boolean = false,
    fontSize: androidx.compose.ui.unit.TextUnit = 20.sp,
    onClick: () -> Unit,
) {
    if (text.isEmpty()) {
        Spacer(modifier = modifier)
        return
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val bgColor = when {
        isClear -> Color(0x33F43F5E)
        isEquals || isPrimary -> MaterialTheme.colorScheme.primary
        isOperator -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val contentColor = when {
        isClear -> MaterialTheme.colorScheme.error
        isEquals || isPrimary -> MaterialTheme.colorScheme.onPrimary
        isOperator -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isPressed) bgColor.copy(alpha = (bgColor.alpha * 1.5f).coerceAtMost(1f)) else bgColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        when (text) {
            "⌫" -> Icon(imageVector = Icons.Default.Backspace, contentDescription = "Delete", tint = contentColor, modifier = Modifier.size(22.dp))
            else -> Text(text = text, fontSize = fontSize, fontWeight = if (isPrimary || isEquals) FontWeight.Bold else FontWeight.Medium, color = contentColor)
        }
    }
}
