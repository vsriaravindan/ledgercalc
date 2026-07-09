package com.example.calchub.ui.screens.margin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calchub.domain.logic.CalculatorLogic
import com.example.calchub.ui.components.CalculatorInput
import com.example.calchub.ui.components.CalculatorScaffold
import com.example.calchub.ui.components.NeonCard
import com.example.calchub.ui.components.ResultRow
import java.text.NumberFormat
import java.util.Locale

/**
 * Screen for calculating margin requirements for trading.
 * Calculates funds needed based on leverage and stock price.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarginCalculatorScreen(onBackClick: () -> Unit) {
    var quantity by remember { mutableDoubleStateOf(100.0) }
    var price by remember { mutableDoubleStateOf(500.0) }
    var leverage by remember { mutableDoubleStateOf(5.0) }
    val results = CalculatorLogic.calculateMargin(quantity, price, leverage)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "Margin Calculator",
        onBackClick = onBackClick,
        calculatorId = "margin"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Quantity", quantity, { quantity = it }, 1.0..10000.0, "Qty")
            CalculatorInput("Price per Share", price, { price = it }, 1.0..100000.0, "₹")
            CalculatorInput("Leverage", leverage, { leverage = it }, 1.0..20.0, "x")
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Margin Required", currencyFormat.format(results.first))
                    ResultRow("Exposure", currencyFormat.format(results.second))
                    ResultRow("Total Value", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
