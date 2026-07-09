package com.example.calchub.ui.screens.salary

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
 * Screen for Salary calculation (In-Hand Salary).
 * Breakdown of Gross Salary, Deductions, and Net Salary.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalaryCalculatorScreen(onBackClick: () -> Unit) {
    var basicSalary by remember { mutableDoubleStateOf(40000.0) }
    var hra by remember { mutableDoubleStateOf(16000.0) }
    var otherAllowances by remember { mutableDoubleStateOf(5000.0) }
    var pf by remember { mutableDoubleStateOf(4800.0) }
    val results = CalculatorLogic.calculateSalary(basicSalary, hra, otherAllowances, pf)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "Salary Calculator",
        onBackClick = onBackClick,
        calculatorId = "salary"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Basic Salary", basicSalary, { basicSalary = it }, 10000.0..500000.0, "₹")
            CalculatorInput("HRA", hra, { hra = it }, 0.0..200000.0, "₹")
            CalculatorInput("Other Allowances", otherAllowances, { otherAllowances = it }, 0.0..100000.0, "₹")
            CalculatorInput("PF Deduction", pf, { pf = it }, 0.0..50000.0, "₹")
            
            DonutChart(listOf(
                DonutChartData(results.second, MaterialTheme.colorScheme.error, "Deductions"),
                DonutChartData(results.third, MaterialTheme.colorScheme.primary, "Take Home")
            ))
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Gross Salary", currencyFormat.format(results.first))
                    ResultRow("Total Deductions", currencyFormat.format(results.second))
                    ResultRow("Net Salary", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
