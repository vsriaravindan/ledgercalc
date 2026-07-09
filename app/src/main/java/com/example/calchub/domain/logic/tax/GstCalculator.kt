package com.example.calchub.domain.logic.tax
/**
 * Calculator logic for Goods and Services Tax (GST).
 * Calculates GST amount and total amount inclusive or exclusive of GST.
 */
object GstCalculator {
    /**
     * Calculates GST.
     *
     * @param amount The base amount or total amount depending on `isInclusive`.
     * @param gstRate The GST rate in percent.
     * @param isInclusive True if the `amount` already includes GST, False otherwise.
     * @return A [Triple] containing:
     *  - First: Original/Base Amount
     *  - Second: GST Amount
     *  - Third: Total Amount (Base + GST)
     */
    fun calculate(amount: Double, gstRate: Double = 18.0, isInclusive: Boolean = false): Triple<Double, Double, Double> {
        val gstAmount = if (isInclusive) amount - (amount / (1 + gstRate / 100)) else amount * (gstRate / 100)
        val totalAmount = if (isInclusive) amount else amount + gstAmount
        return Triple(amount, gstAmount, totalAmount)
    }
}
