package com.example.calchub.ui.screens.post_office_mis

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
 * Screen for Post Office Monthly Income Scheme (MIS) calculation.
 * Calculates monthly and total income based on investment.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostOfficeMisCalculatorScreen(onBackClick: () -> Unit) {
    var investment by remember { mutableDoubleStateOf(450000.0) }
    var interestRate by remember { mutableDoubleStateOf(7.4) }
    val results = CalculatorLogic.calculatePostOfficeMIS(investment, interestRate)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "Post Office MIS",
        onBackClick = onBackClick,
        calculatorId = "post_office_mis"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Investment Amount", investment, { investment = it }, 1000.0..900000.0, "₹")
            CalculatorInput("Interest Rate", interestRate, { interestRate = it }, 1.0..15.0, "%")
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Investment", currencyFormat.format(results.first))
                    ResultRow("Monthly Income", currencyFormat.format(results.second))
                    ResultRow("Total Income (5 Yr)", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
