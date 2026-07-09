package com.example.calchub.domain.logic.investment

import kotlin.math.pow

/**
 * Calculator logic for Employee Provident Fund (EPF).
 * EPF is a retirement savings scheme for salaried employees in India.
 */
object EpfCalculator {
    /**
     * Calculates the estimated returns and total value for EPF investment.
     *
     * @param monthlyContribution The amount contributed monthly by the employee/employer.
     * @param interestRate The annual interest rate in percent (default is 8.25%).
     * @param timePeriodYears The duration of the investment in years.
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns (Interest Earned)
     *  - Third: Total Value (Maturity Amount)
     */
    fun calculate(
        monthlyContribution: Double,
        interestRate: Double = 8.25,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        val i = interestRate / 100
        val n = timePeriodYears
        val yearlyContribution = monthlyContribution * 12
        
        val totalValue = yearlyContribution * ((1 + i).pow(n) - 1) / i * (1 + i)
        val investedAmount = yearlyContribution * n
        val estimatedReturns = totalValue - investedAmount
        
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
