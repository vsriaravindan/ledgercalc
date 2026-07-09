package com.example.calchub.ui.screens.apy

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
 * Screen for calculating Atal Pension Yojana (APY) contributions and returns.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApyCalculatorScreen(onBackClick: () -> Unit) {
    var currentAge by remember { mutableDoubleStateOf(25.0) }
    var pensionAmount by remember { mutableDoubleStateOf(5000.0) }
    val results = CalculatorLogic.calculateAPY(currentAge, pensionAmount)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "APY Calculator",
        onBackClick = onBackClick,
        calculatorId = "apy"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Current Age", currentAge, { currentAge = it }, 18.0..40.0, "Yr")
            CalculatorInput("Desired Pension", pensionAmount, { pensionAmount = it }, 1000.0..5000.0, "₹")
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Monthly Contribution", currencyFormat.format(results.first))
                    ResultRow("Total Contribution", currencyFormat.format(results.second))
                    ResultRow("Monthly Pension at 60", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
