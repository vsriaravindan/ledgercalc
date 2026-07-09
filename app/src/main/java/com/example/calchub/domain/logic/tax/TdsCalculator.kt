package com.example.calchub.domain.logic.tax
/**
 * Calculator logic for Tax Deducted at Source (TDS).
 * Calculates the amount deducted as tax from payments like salary, rent, etc.
 */
object TdsCalculator {
    /**
     * Calculates TDS Amount.
     *
     * @param amount The gross amount.
     * @param tdsRate The TDS rate in percent.
     * @return A [Triple] containing:
     *  - First: Gross Amount
     *  - Second: TDS Amount Deducted
     *  - Third: Net Amount Payable
     */
    fun calculate(amount: Double, tdsRate: Double = 10.0): Triple<Double, Double, Double> {
        val tdsAmount = amount * (tdsRate / 100)
        val netAmount = amount - tdsAmount
        return Triple(amount, tdsAmount, netAmount)
    }
}
