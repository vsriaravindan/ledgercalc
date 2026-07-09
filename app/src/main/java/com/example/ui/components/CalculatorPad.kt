package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CalculatorPad(
    onInput: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onEvaluate: () -> Unit,
    onEquals: () -> Unit,
    onScientificToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val buttons = listOf(
        listOf("C", "÷", "×", "DEL"),
        listOf("7", "8", "9", "-"),
        listOf("4", "5", "6", "+"),
        listOf("1", "2", "3", "="),
        listOf("0", ".", "", "")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttons.take(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { btn ->
                    val isOperator = btn in listOf("C", "DEL", "÷", "×", "-", "+")
                    val color = if (isOperator) CalcOperatorBg else CalcNumberBg
                    val textColor = if (isOperator) CalcOperatorContent else CalcNumberContent
                    
                    CalcButton(
                        text = btn,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        color = color,
                        textColor = textColor,
                        onClick = {
                            when (btn) {
                                "C" -> onClear()
                                "DEL" -> onDelete()
                                else -> onInput(btn)
                            }
                        }
                    )
                }
            }
        }
        
        // Handle bottom two rows specially for the tall equals/save button
        Row(
            modifier = Modifier.fillMaxWidth().weight(2f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(3f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "1", "2", "3"
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("1", "2", "3").forEach { btn ->
                        CalcButton(
                            text = btn,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            color = CalcNumberBg,
                            textColor = CalcNumberContent,
                            onClick = { onInput(btn) }
                        )
                    }
                }
                // "0", "."
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalcButton(
                        text = "0",
                        modifier = Modifier.weight(2f).fillMaxHeight(), // Double width
                        color = CalcNumberBg,
                        textColor = CalcNumberContent,
                        onClick = { onInput("0") }
                    )
                    CalcButton(
                        text = ".",
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        color = CalcNumberBg,
                        textColor = CalcNumberContent,
                        onClick = { onInput(".") }
                    )
                }
            }
            
            // "=" and "SAVE" buttons
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton(
                    text = "=",
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    color = CalcOperatorBg,
                    textColor = CalcOperatorContent,
                    onClick = onEvaluate
                )
                CalcButton(
                    text = "SAVE",
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    color = BentoPrimary,
                    textColor = BentoOnPrimary,
                    fontSize = 18.sp,
                    onClick = onEquals
                )
            }
        }
    }
}

@Composable
fun CalcButton(
    text: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = CalcNumberBg,
    textColor: androidx.compose.ui.graphics.Color = CalcNumberContent,
    fontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    if (text.isEmpty()) {
        Spacer(modifier = modifier)
        return
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .border(1.dp, androidx.compose.ui.graphics.Color(0x33FFFFFF), androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
            .clickable { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onClick() },
        contentAlignment = Alignment.Center
    ) {
        val label = if (text == "DEL") "⌫" else text
        Text(
            text = label,
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
