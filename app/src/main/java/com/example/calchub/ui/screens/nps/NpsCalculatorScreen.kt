package com.example.calchub.ui.screens.nps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.calchub.ui.components.ResultCard

/**
 * Screen for calculating National Pension System (NPS) corpus.
 * Projects retirement corpus based on monthly contribution and age.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NpsCalculatorScreen(onBackClick: () -> Unit) {
    var monthlyContribution by remember { mutableDoubleStateOf(5000.0) }
    var expectedReturnRate by remember { mutableDoubleStateOf(10.0) }
    var currentAge by remember { mutableDoubleStateOf(30.0) }
    var retirementAge by remember { mutableDoubleStateOf(60.0) }

    val results = CalculatorLogic.calculateNPS(monthlyContribution, expectedReturnRate, currentAge, retirementAge)

    CalculatorScaffold(
        title = "NPS Calculator",
        onBackClick = onBackClick,
        calculatorId = "nps"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CalculatorInput(
                label = "Monthly Contribution",
                value = monthlyContribution,
                onValueChange = { monthlyContribution = it },
                range = 500.0..50000.0,
                symbol = "₹"
            )

            CalculatorInput(
                label = "Expected Return Rate (p.a)",
                value = expectedReturnRate,
                onValueChange = { expectedReturnRate = it },
                range = 1.0..20.0,
                symbol = "%"
            )

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
                range = 60.0..70.0,
                symbol = "Yr"
            )

            DonutChart(
                data = listOf(
                    DonutChartData(results.first, MaterialTheme.colorScheme.primary, "Invested"),
                    DonutChartData(results.second, MaterialTheme.colorScheme.tertiary, "Returns")
                )
            )

            ResultCard(
                investedAmount = results.first,
                estimatedReturns = results.second,
                totalValue = results.third
            )
        }
    }
}
