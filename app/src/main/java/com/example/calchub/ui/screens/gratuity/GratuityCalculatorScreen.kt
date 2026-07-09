package com.example.calchub.ui.screens.gratuity

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
 * Screen for calculating Gratuity benefits based on salary and tenure.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GratuityCalculatorScreen(onBackClick: () -> Unit) {
    var lastSalary by remember { mutableDoubleStateOf(50000.0) }
    var yearsOfService by remember { mutableDoubleStateOf(10.0) }
    val results = CalculatorLogic.calculateGratuity(lastSalary, yearsOfService)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "Gratuity Calculator",
        onBackClick = onBackClick,
        calculatorId = "gratuity"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Last Drawn Salary", lastSalary, { lastSalary = it }, 10000.0..500000.0, "₹")
            CalculatorInput("Years of Service", yearsOfService, { yearsOfService = it }, 1.0..40.0, "Yr")
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Total Gratuity", currencyFormat.format(results.first))
                    ResultRow("Tax Free Amount", currencyFormat.format(results.second))
                    ResultRow("Taxable Amount", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
