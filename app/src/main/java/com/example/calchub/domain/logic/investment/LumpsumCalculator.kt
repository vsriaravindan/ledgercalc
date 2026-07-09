package com.example.calchub.domain.logic.investment

import kotlin.math.pow

/**
 * Calculator logic for Lumpsum Mutual Fund Investment.
 * Calculates the future value of a one-time investment based on an expected annual return rate.
 */
object LumpsumCalculator {
    /**
     * Calculates Lumpsum Investment Returns using the compound interest formula.
     *
     * @param totalInvestment Total amount invested as a lumpsum.
     * @param expectedReturnRate Annual expected return rate (CAGR) in percent.
     * @param timePeriodYears Time period of investment in years.
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns (Profit)
     *  - Third: Total Value (Maturity Amount)
     */
    fun calculate(
        totalInvestment: Double,
        expectedReturnRate: Double,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        val totalValue = totalInvestment * (1 + expectedReturnRate / 100).pow(timePeriodYears)
        val estimatedReturns = totalValue - totalInvestment
        
        return Triple(totalInvestment, estimatedReturns, totalValue)
    }
}
