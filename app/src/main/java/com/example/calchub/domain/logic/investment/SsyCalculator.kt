package com.example.calchub.domain.logic.investment

import kotlin.math.pow

/**
 * Calculator logic for Sukanya Samriddhi Yojana (SSY).
 * SSY is a government-backed savings scheme for the girl child.
 */
object SsyCalculator {
    /**
     * Calculates the maturity amount and interest earned for SSY.
     *
     * @param yearlyDeposit The amount deposited yearly.
     * @param interestRate The annual interest rate in percent (default is 8.2%).
     * @param timePeriodYears The duration of the investment in years (default is 15 years for deposits, though maturity is 21 years).
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns (Interest Earned)
     *  - Third: Total Value (Maturity Amount)
     */
    fun calculate(
        yearlyDeposit: Double,
        interestRate: Double = 8.2,
        timePeriodYears: Double = 15.0
    ): Triple<Double, Double, Double> {
        val i = interestRate / 100
        val n = timePeriodYears
        
        val totalValue = yearlyDeposit * ((1 + i).pow(n) - 1) / i * (1 + i)
        val investedAmount = yearlyDeposit * n
        val estimatedReturns = totalValue - investedAmount
        
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
