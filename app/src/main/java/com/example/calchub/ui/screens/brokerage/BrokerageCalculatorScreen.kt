package com.example.calchub.ui.screens.brokerage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
 * Screen for calculating Stock Brokerage charges.
 * Supports Delivery and Intraday calculations.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrokerageCalculatorScreen(onBackClick: () -> Unit) {
    var tradeValue by remember { mutableDoubleStateOf(100000.0) }
    var brokerageRate by remember { mutableDoubleStateOf(0.03) }
    var isIntraday by remember { mutableStateOf(false) }
    val results = CalculatorLogic.calculateBrokerage(tradeValue, brokerageRate, isIntraday)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "Brokerage Calculator",
        onBackClick = onBackClick,
        calculatorId = "brokerage"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Trade Value", tradeValue, { tradeValue = it }, 1000.0..10000000.0, "₹")
            CalculatorInput("Brokerage Rate", brokerageRate, { brokerageRate = it }, 0.01..1.0, "%")
            
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(isIntraday, { isIntraday = it })
                Text("Intraday Trade")
            }
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Trade Value", currencyFormat.format(results.first))
                    ResultRow("Total Charges", currencyFormat.format(results.second))
                    ResultRow("Net Amount", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
