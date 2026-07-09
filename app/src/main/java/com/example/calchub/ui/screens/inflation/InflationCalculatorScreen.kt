package com.example.calchub.ui.screens.inflation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.example.calchub.ui.components.DonutChart
import com.example.calchub.ui.components.DonutChartData
import com.example.calchub.ui.components.NeonCard
import com.example.calchub.ui.components.ResultRow
import java.text.NumberFormat
import java.util.Locale

/**
 * Screen for calculating the impact of inflation over time.
 * Calculates future value of money based on inflation rate.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InflationCalculatorScreen(onBackClick: () -> Unit) {
    var currentPrice by remember { mutableDoubleStateOf(10000.0) }
    var inflationRate by remember { mutableDoubleStateOf(6.0) }
    var years by remember { mutableDoubleStateOf(10.0) }
    val results = CalculatorLogic.calculateInflation(currentPrice, inflationRate, years)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "Inflation Calculator",
        onBackClick = onBackClick,
        calculatorId = "inflation"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Current Price", currentPrice, { currentPrice = it }, 100.0..10000000.0, "₹")
            CalculatorInput("Inflation Rate", inflationRate, { inflationRate = it }, 1.0..20.0, "%")
            CalculatorInput("Time Period", years, { years = it }, 1.0..50.0, "Yr")
            
            DonutChart(listOf(
                DonutChartData(currentPrice, MaterialTheme.colorScheme.primary, "Current"),
                DonutChartData(results.second, MaterialTheme.colorScheme.tertiary, "Increase")
            ))
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Current Price", currencyFormat.format(results.first))
                    ResultRow("Price Increase", currencyFormat.format(results.second))
                    ResultRow("Future Price", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
