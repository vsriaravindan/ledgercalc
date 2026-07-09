package com.example.calchub.domain.logic.investment

import kotlin.math.pow

/**
 * Calculator logic for Fixed Deposit (FD).
 * FD is a financial instrument provided by banks which provides investors a higher rate of interest than a regular savings account, until the given maturity date.
 */
object FdCalculator {
    /**
     * Calculates the maturity amount and interest earned for a Fixed Deposit.
     *
     * @param totalInvestment The principal amount deposited.
     * @param rateOfInterest The annual interest rate in percent.
     * @param timePeriodYears The duration of the deposit in years.
     * @param compoundingFrequency The number of times interest is compounded per year (default is 4 for quarterly).
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns (Interest Earned)
     *  - Third: Total Value (Maturity Amount)
     */
    fun calculate(
        totalInvestment: Double,
        rateOfInterest: Double,
        timePeriodYears: Double,
        compoundingFrequency: Int = 4
    ): Triple<Double, Double, Double> {
        val r = rateOfInterest / 100
        val n = compoundingFrequency
        val t = timePeriodYears
        
        val totalValue = totalInvestment * (1 + r / n).pow(n * t)
        val estimatedReturns = totalValue - totalInvestment
        
        return Triple(totalInvestment, estimatedReturns, totalValue)
    }
}
