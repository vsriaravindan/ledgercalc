package com.example.calchub.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.BeachAccess
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.CompareArrows
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Elderly
import androidx.compose.material.icons.rounded.ElderlyWoman
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.HolidayVillage
import androidx.compose.material.icons.rounded.House
import androidx.compose.material.icons.rounded.LocalPostOffice
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.RequestPage
import androidx.compose.material.icons.rounded.RequestQuote
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material.icons.rounded.TimeToLeave
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Woman
import androidx.compose.material.icons.rounded.WorkHistory
import androidx.compose.ui.graphics.vector.ImageVector

enum class CalculatorCategory {
    POPULAR, INVESTMENT, LOAN, TAX, RETIREMENT, TRADING, OTHER
}

data class Calculator(
    val name: String,
    val route: String,
    val category: CalculatorCategory,
    val isPopular: Boolean = false,
    val icon: ImageVector = Icons.Rounded.Calculate,
)

val allCalculators = listOf(
    // Investment
    Calculator("SIP", "sip", CalculatorCategory.INVESTMENT, true, Icons.Rounded.TrendingUp),
    Calculator("Lumpsum", "lumpsum", CalculatorCategory.INVESTMENT, false, Icons.Rounded.AutoGraph),
    Calculator("FD", "fd", CalculatorCategory.INVESTMENT, true, Icons.Rounded.Lock),
    Calculator("PPF", "ppf", CalculatorCategory.INVESTMENT, true, Icons.Rounded.Shield),
    Calculator("RD", "rd", CalculatorCategory.INVESTMENT, false, Icons.Rounded.Refresh),
    Calculator("SSY", "ssy", CalculatorCategory.INVESTMENT, false, Icons.Rounded.Woman),
    Calculator("EPF", "epf", CalculatorCategory.INVESTMENT, false, Icons.Rounded.WorkHistory),
    Calculator("NSC", "nsc", CalculatorCategory.INVESTMENT, false, Icons.Rounded.Verified),
    Calculator("SWP", "swp", CalculatorCategory.INVESTMENT, false, Icons.Rounded.Logout),
    Calculator("MF Returns", "mf_returns", CalculatorCategory.INVESTMENT, false, Icons.Rounded.ShowChart),

    // Loan
    Calculator("EMI", "emi", CalculatorCategory.LOAN, true, Icons.Rounded.CreditCard),
    Calculator("Home Loan", "home_loan_emi", CalculatorCategory.LOAN, true, Icons.Rounded.House),
    Calculator("Car Loan", "car_loan_emi", CalculatorCategory.LOAN, false, Icons.Rounded.TimeToLeave),
    Calculator("Flat vs Reducing", "flat_vs_reducing", CalculatorCategory.LOAN, false, Icons.Rounded.CompareArrows),

    // Tax
    Calculator("Income Tax", "income_tax", CalculatorCategory.TAX, true, Icons.Rounded.Receipt),
    Calculator("GST", "gst", CalculatorCategory.TAX, true, Icons.Rounded.RequestQuote),
    Calculator("HRA", "hra", CalculatorCategory.TAX, false, Icons.Rounded.HolidayVillage),
    Calculator("TDS", "tds", CalculatorCategory.TAX, false, Icons.Rounded.ContentCut),

    // Retirement
    Calculator("NPS", "nps", CalculatorCategory.RETIREMENT, true, Icons.Rounded.Elderly),
    Calculator("Retirement", "retirement", CalculatorCategory.RETIREMENT, false, Icons.Rounded.BeachAccess),
    Calculator("Gratuity", "gratuity", CalculatorCategory.RETIREMENT, false, Icons.Rounded.CardGiftcard),
    Calculator("APY", "apy", CalculatorCategory.RETIREMENT, false, Icons.Rounded.Security),

    // Trading
    Calculator("Brokerage", "brokerage", CalculatorCategory.TRADING, false, Icons.Rounded.RequestPage),
    Calculator("Stock Average", "stock_average", CalculatorCategory.TRADING, false, Icons.Rounded.Functions),
    Calculator("CAGR", "cagr", CalculatorCategory.TRADING, false, Icons.Rounded.Timeline),
    Calculator("XIRR", "xirr", CalculatorCategory.TRADING, false, Icons.Rounded.BarChart),
    Calculator("Margin", "margin", CalculatorCategory.TRADING, false, Icons.Rounded.AccountBalance),

    // Other
    Calculator("Simple Interest", "simple_interest", CalculatorCategory.OTHER, true, Icons.Rounded.Percent),
    Calculator("Compound Interest", "compound_interest", CalculatorCategory.OTHER, false, Icons.Rounded.Loop),
    Calculator("Salary", "salary", CalculatorCategory.OTHER, false, Icons.Rounded.AttachMoney),
    Calculator("Inflation", "inflation", CalculatorCategory.OTHER, false, Icons.Rounded.TrendingDown),
    Calculator("Post Office MIS", "post_office_mis", CalculatorCategory.OTHER, false, Icons.Rounded.LocalPostOffice),
    Calculator("SCSS", "scss", CalculatorCategory.OTHER, false, Icons.Rounded.ElderlyWoman),
)
