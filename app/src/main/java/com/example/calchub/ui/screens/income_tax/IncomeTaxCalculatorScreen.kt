package com.example.calchub.ui.screens.income_tax

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
 * Screen for calculating Income Tax liability.
 * Simplified calculation based on income and deductions.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeTaxCalculatorScreen(onBackClick: () -> Unit) {
    var annualIncome by remember { mutableDoubleStateOf(1000000.0) }
    var deductions by remember { mutableDoubleStateOf(150000.0) }

    val results = CalculatorLogic.calculateIncomeTax(annualIncome, deductions)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "Income Tax Calculator",
        onBackClick = onBackClick,
        calculatorId = "income_tax"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CalculatorInput(
                label = "Annual Income",
                value = annualIncome,
                onValueChange = { annualIncome = it },
                range = 100000.0..10000000.0,
                symbol = "₹"
            )

            CalculatorInput(
                label = "Deductions (80C, etc.)",
                value = deductions,
                onValueChange = { deductions = it },
                range = 0.0..500000.0,
                symbol = "₹"
            )

            DonutChart(
                data = listOf(
                    DonutChartData(results.second, MaterialTheme.colorScheme.error, "Tax"),
                    DonutChartData(results.third, MaterialTheme.colorScheme.primary, "Take Home")
                )
            )

            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ResultRow("Gross Income", currencyFormat.format(results.first))
                    ResultRow("Tax Payable", currencyFormat.format(results.second))
                    ResultRow("Net Income", currencyFormat.format(results.third), isTotal = true)
                }
            }
        }
    }
}
