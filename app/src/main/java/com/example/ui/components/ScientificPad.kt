package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalcNumberBg
import com.example.ui.theme.CalcNumberContent

@Composable
fun ScientificPad(
    onInput: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttons = listOf(
        listOf("(", ")", "^", "√"),
        listOf("sin", "cos", "tan", "log")
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { btn ->
                    CalcButton(
                        text = btn,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        color = CalcNumberBg,
                        textColor = CalcNumberContent,
                        fontSize = 18.sp,
                        onClick = { onInput(btn) }
                    )
                }
            }
        }
    }
}
