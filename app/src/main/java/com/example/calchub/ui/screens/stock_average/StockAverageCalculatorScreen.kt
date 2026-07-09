package com.example.calchub.ui.screens.stock_average

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
 * Screen for Stock Average calculation.
 * Calculates average price of stocks bought at different prices.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAverageCalculatorScreen(onBackClick: () -> Unit) {
    var firstQuantity by remember { mutableDoubleStateOf(100.0) }
    var firstPrice by remember { mutableDoubleStateOf(150.0) }
    var secondQuantity by remember { mutableDoubleStateOf(50.0) }
    var secondPrice by remember { mutableDoubleStateOf(120.0) }
    val results = CalculatorLogic.calculateStockAverage(firstQuantity, firstPrice, secondQuantity, secondPrice)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "Stock Average Calculator",
        onBackClick = onBackClick,
        calculatorId = "stock_average"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("First Purchase", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
            CalculatorInput("Quantity", firstQuantity, { firstQuantity = it }, 1.0..10000.0, "Qty")
            CalculatorInput("Price per Share", firstPrice, { firstPrice = it }, 1.0..100000.0, "₹")
            
            Text("Second Purchase", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
            CalculatorInput("Quantity", secondQuantity, { secondQuantity = it }, 1.0..10000.0, "Qty")
            CalculatorInput("Price per Share", secondPrice, { secondPrice = it }, 1.0..100000.0, "₹")
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Total Quantity", String.format("%.0f shares", results.first))
                    ResultRow("Average Price", currencyFormat.format(results.second))
                    ResultRow("Total Investment", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
