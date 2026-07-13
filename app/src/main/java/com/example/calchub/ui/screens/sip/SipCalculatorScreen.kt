package com.example.calchub.ui.screens.sip

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
 * Screen for Systematic Investment Plan (SIP) calculation.
 * Calculates returns for regular monthly investments.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SipCalculatorScreen(onBackClick: () -> Unit) {
    var monthlyInvestment by remember { mutableDoubleStateOf(5000.0) }
    var expectedReturnRate by remember { mutableDoubleStateOf(12.0) }
    var timePeriodYears by remember { mutableDoubleStateOf(10.0) }

    val results = CalculatorLogic.calculateSIP(monthlyInvestment, expectedReturnRate, timePeriodYears)
    val shareContent = remember(results) {
        "📊 SIP Calculation\n" +
        "Monthly: ₹${monthlyInvestment.toInt()}\n" +
        "Rate: ${expectedReturnRate}%\n" +
        "Period: ${timePeriodYears.toInt()} years\n" +
        "Invested: ₹${results.first.toInt()}\n" +
        "Returns: ₹${results.second.toInt()}\n" +
        "Total: ₹${results.third.toInt()}"
    }
    val context = androidx.compose.ui.platform.LocalContext.current

    CalculatorScaffold(
        title = "SIP Calculator",
        onBackClick = onBackClick,
        calculatorId = "sip",
        onShare = {
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_TEXT, shareContent)
            }
            context.startActivity(android.content.Intent.createChooser(intent, "Share SIP Result"))
        },
        shareContent = shareContent,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CalculatorInput(
                label = "Monthly Investment",
                value = monthlyInvestment,
                onValueChange = { monthlyInvestment = it },
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
                range = 1.0..40.0,
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
