package com.example.calchub.ui.screens.flat_vs_reducing

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
 * Screen for comparing Flat Rate vs Reducing Rate loans.
 * Displays the difference in total interest payable.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlatVsReducingCalculatorScreen(onBackClick: () -> Unit) {
    var loanAmount by remember { mutableDoubleStateOf(500000.0) }
    var flatRate by remember { mutableDoubleStateOf(10.0) }
    var tenureYears by remember { mutableDoubleStateOf(5.0) }
    val results = CalculatorLogic.calculateFlatVsReducing(loanAmount, flatRate, tenureYears)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "Flat vs Reducing Rate",
        onBackClick = onBackClick,
        calculatorId = "flat_vs_reducing"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Loan Amount", loanAmount, { loanAmount = it }, 10000.0..10000000.0, "₹")
            CalculatorInput("Flat Interest Rate", flatRate, { flatRate = it }, 1.0..30.0, "%")
            CalculatorInput("Tenure", tenureYears, { tenureYears = it }, 1.0..30.0, "Yr")
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Flat Rate EMI", currencyFormat.format(results.first))
                    ResultRow("Reducing Rate EMI", currencyFormat.format(results.second))
                    ResultRow("Difference", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
