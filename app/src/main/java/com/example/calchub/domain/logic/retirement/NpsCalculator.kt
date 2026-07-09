package com.example.calchub.domain.logic.retirement
import kotlin.math.pow
/**
 * Calculator logic for National Pension System (NPS).
 * NPS is a voluntary, long-term retirement savings scheme designed to enable systematic savings.
 */
object NpsCalculator {
    /**
     * Calculates the estimated corpus and returns for NPS.
     *
     * @param monthlyContribution The monthly contribution amount.
     * @param expectedReturnRate The annual expected return rate in percent.
     * @param currentAge The current age of the subscriber.
     * @param retirementAge The age of retirement (default 60).
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns
     *  - Third: Total Maturity Value
     */
    fun calculate(monthlyContribution: Double, expectedReturnRate: Double, currentAge: Double, retirementAge: Double = 60.0): Triple<Double, Double, Double> {
        val timePeriodYears = retirementAge - currentAge
        val i = expectedReturnRate / 12 / 100
        val n = timePeriodYears * 12
        val totalValue = monthlyContribution * ((1 + i).pow(n) - 1) / i * (1 + i)
        val investedAmount = monthlyContribution * n
        val estimatedReturns = totalValue - investedAmount
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
