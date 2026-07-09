package com.example.calchub.domain.logic.investment

import kotlin.math.pow

/**
 * Calculator logic for Systematic Investment Plan (SIP).
 * SIP is an investment route offered by Mutual Funds wherein one can invest a fixed amount in a Mutual Fund scheme at regular intervals.
 */
object SipCalculator {
    /**
     * Calculates SIP Returns.
     *
     * @param monthlyInvestment Amount invested per month.
     * @param expectedReturnRate Annual expected return rate (in percentage).
     * @param timePeriodYears Time period of investment in years.
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns (Profit)
     *  - Third: Total Value (Maturity Amount)
     */
    fun calculate(
        monthlyInvestment: Double,
        expectedReturnRate: Double,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        val i = expectedReturnRate / 12 / 100
        val n = timePeriodYears * 12
        
        val totalValue = monthlyInvestment * ((1 + i).pow(n) - 1) / i * (1 + i)
        val investedAmount = monthlyInvestment * n
        val estimatedReturns = totalValue - investedAmount
        
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
