package com.example.calchub.ui.screens.gst

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
 * Screen for calculating Goods and Services Tax (GST).
 * Supports exclusive and inclusive GST calculations.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GstCalculatorScreen(onBackClick: () -> Unit) {
    var amount by remember { mutableDoubleStateOf(10000.0) }
    var gstRate by remember { mutableDoubleStateOf(18.0) }
    var isInclusive by remember { mutableStateOf(false) }
    val results = CalculatorLogic.calculateGST(amount, gstRate, isInclusive)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "GST Calculator",
        onBackClick = onBackClick,
        calculatorId = "gst"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Amount", amount, { amount = it }, 100.0..10000000.0, "₹")
            CalculatorInput("GST Rate", gstRate, { gstRate = it }, 0.0..28.0, "%")
            
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(isInclusive, { isInclusive = it })
                Text("GST Inclusive")
            }
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Base Amount", currencyFormat.format(results.first))
                    ResultRow("GST Amount", currencyFormat.format(results.second))
                    ResultRow("Total Amount", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
