package com.example.calchub.domain.logic.investment

import kotlin.math.pow

/**
 * Calculator logic for National Savings Certificate (NSC).
 * NSC is a fixed income investment scheme that you can open with any post office.
 */
object NscCalculator {
    /**
     * Calculates the maturity amount and interest earned for NSC.
     *
     * @param investment The total amount invested.
     * @param interestRate The annual interest rate in percent (default is 7.7%).
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns (Interest Earned)
     *  - Third: Total Value (Maturity Amount)
     */
    fun calculate(
        investment: Double,
        interestRate: Double = 7.7
    ): Triple<Double, Double, Double> {
        val tenure = 5.0
        val totalValue = investment * (1 + interestRate / 100).pow(tenure)
        val interest = totalValue - investment
        return Triple(investment, interest, totalValue)
    }
}
