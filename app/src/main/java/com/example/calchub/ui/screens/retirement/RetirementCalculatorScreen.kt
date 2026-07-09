package com.example.calchub.ui.screens.retirement

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
 * Screen for Retirement planning calculation.
 * Estimates corpus needed and monthly savings required for retirement.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetirementCalculatorScreen(onBackClick: () -> Unit) {
    var currentAge by remember { mutableDoubleStateOf(30.0) }
    var retirementAge by remember { mutableDoubleStateOf(60.0) }
    var monthlyExpenses by remember { mutableDoubleStateOf(50000.0) }
    var inflationRate by remember { mutableDoubleStateOf(6.0) }
    var expectedReturn by remember { mutableDoubleStateOf(12.0) }

    val results = CalculatorLogic.calculateRetirement(currentAge, retirementAge, monthlyExpenses, inflationRate, expectedReturn)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "Retirement Calculator",
        onBackClick = onBackClick,
        calculatorId = "retirement"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CalculatorInput(
                label = "Current Age",
                value = currentAge,
                onValueChange = { currentAge = it },
                range = 18.0..55.0,
                symbol = "Yr"
            )

            CalculatorInput(
                label = "Retirement Age",
                value = retirementAge,
                onValueChange = { retirementAge = it },
                range = 55.0..70.0,
                symbol = "Yr"
            )

            CalculatorInput(
                label = "Monthly Expenses",
                value = monthlyExpenses,
                onValueChange = { monthlyExpenses = it },
                range = 10000.0..500000.0,
                symbol = "₹"
            )

            CalculatorInput(
                label = "Inflation Rate",
                value = inflationRate,
                onValueChange = { inflationRate = it },
                range = 1.0..15.0,
                symbol = "%"
            )

            CalculatorInput(
                label = "Expected Return",
                value = expectedReturn,
                onValueChange = { expectedReturn = it },
                range = 1.0..20.0,
                symbol = "%"
            )

            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ResultRow("Monthly SIP Needed", currencyFormat.format(results.first))
                    ResultRow("Corpus Needed", currencyFormat.format(results.second))
                    ResultRow("Future Annual Expenses", currencyFormat.format(results.third), isTotal = true)
                }
            }
        }
    }
}
