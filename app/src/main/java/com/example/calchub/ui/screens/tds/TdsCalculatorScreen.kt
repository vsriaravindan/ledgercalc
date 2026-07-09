package com.example.calchub.ui.screens.tds

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
 * Screen for Tax Deducted at Source (TDS) calculation.
 * Calculates TDS amount and net payout based on gross amount and rate.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TdsCalculatorScreen(onBackClick: () -> Unit) {
    var amount by remember { mutableDoubleStateOf(100000.0) }
    var tdsRate by remember { mutableDoubleStateOf(10.0) }
    val results = CalculatorLogic.calculateTDS(amount, tdsRate)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "TDS Calculator",
        onBackClick = onBackClick,
        calculatorId = "tds"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Gross Amount", amount, { amount = it }, 1000.0..10000000.0, "₹")
            CalculatorInput("TDS Rate", tdsRate, { tdsRate = it }, 1.0..30.0, "%")
            
            DonutChart(listOf(
                DonutChartData(results.second, MaterialTheme.colorScheme.error, "TDS"),
                DonutChartData(results.third, MaterialTheme.colorScheme.primary, "Net Amount")
            ))
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Gross Amount", currencyFormat.format(results.first))
                    ResultRow("TDS Deducted", currencyFormat.format(results.second))
                    ResultRow("Net Amount", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
