package com.example.calchub.ui.screens.swp

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
 * Screen for Systematic Withdrawal Plan (SWP) calculation.
 * Shows withdrawal schedule and remaining balance over time.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwpCalculatorScreen(onBackClick: () -> Unit) {
    var totalInvestment by remember { mutableDoubleStateOf(500000.0) }
    var withdrawalPerMonth by remember { mutableDoubleStateOf(5000.0) }
    var expectedReturnRate by remember { mutableDoubleStateOf(8.0) }
    var timePeriodYears by remember { mutableDoubleStateOf(10.0) }

    val results = CalculatorLogic.calculateSWP(totalInvestment, withdrawalPerMonth, expectedReturnRate, timePeriodYears)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "SWP Calculator",
        onBackClick = onBackClick,
        calculatorId = "swp"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CalculatorInput(
                label = "Total Investment",
                value = totalInvestment,
                onValueChange = { totalInvestment = it },
                range = 10000.0..10000000.0,
                symbol = "₹"
            )

            CalculatorInput(
                label = "Withdrawal per Month",
                value = withdrawalPerMonth,
                onValueChange = { withdrawalPerMonth = it },
                range = 500.0..100000.0,
                symbol = "₹"
            )

            CalculatorInput(
                label = "Expected Return Rate (p.a)",
                value = expectedReturnRate,
                onValueChange = { expectedReturnRate = it },
                range = 1.0..30.0,
                symbol = "%"
            )

            CalculatorInput(
                label = "Time Period",
                value = timePeriodYears,
                onValueChange = { timePeriodYears = it },
                range = 1.0..30.0,
                symbol = "Yr"
            )

            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ResultRow("Initial Investment", currencyFormat.format(results.first))
                    ResultRow("Total Withdrawn", currencyFormat.format(results.second))
                    ResultRow("Final Balance", currencyFormat.format(results.third), isTotal = true)
                }
            }
        }
    }
}
