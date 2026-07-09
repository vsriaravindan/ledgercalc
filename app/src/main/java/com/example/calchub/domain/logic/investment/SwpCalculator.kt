package com.example.calchub.domain.logic.investment

/**
 * Calculator logic for Systematic Withdrawal Plan (SWP).
 * SWP allows an investor to withdraw a fixed or variable amount from their mutual fund scheme on a monthly, quarterly, or annual basis.
 */
object SwpCalculator {
    /**
     * Calculates the remaining balance and total withdrawn amount for SWP.
     *
     * @param totalInvestment The initial lumpsum amount invested.
     * @param withdrawalPerMonth The amount withdrawn monthly.
     * @param expectedReturnRate The expected annual return rate on the remaining balance.
     * @param timePeriodYears The duration of the withdrawal plan in years.
     * @return A [Triple] containing:
     *  - First: Total Investment (Initial Value)
     *  - Second: Total Withdrawn Amount
     *  - Third: Final Balance Amount
     */
    fun calculate(
        totalInvestment: Double,
        withdrawalPerMonth: Double,
        expectedReturnRate: Double,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        val r = expectedReturnRate / 12 / 100
        val n = timePeriodYears * 12
        
        var balance = totalInvestment
        var totalWithdrawn = 0.0
        
        for (month in 1..n.toInt()) {
            balance = balance * (1 + r) - withdrawalPerMonth
            totalWithdrawn += withdrawalPerMonth
            if (balance < 0) balance = 0.0
        }
        
        return Triple(totalInvestment, totalWithdrawn, balance)
    }
}
