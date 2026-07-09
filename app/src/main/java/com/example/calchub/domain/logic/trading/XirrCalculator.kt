package com.example.calchub.domain.logic.trading
/**
 * Calculator logic for XIRR (Extended Internal Rate of Return).
 * Simplified implementation for return percentage calculation.
 */
object XirrCalculator {
    /**
     * Calculates simplified Return Percentage (since full XIRR requires dates).
     *
     * @param investments List of investment amounts.
     * @param returns The final value or returns amount.
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Absolute Profit
     *  - Third: Return Percentage
     */
    fun calculate(investments: List<Double>, returns: Double): Triple<Double, Double, Double> {
        val totalInvested = investments.sum()
        val profit = returns - totalInvested
        val returnPercent = (profit / totalInvested) * 100
        return Triple(totalInvested, profit, returnPercent)
    }
}
