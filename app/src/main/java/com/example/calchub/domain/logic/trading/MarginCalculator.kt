package com.example.calchub.domain.logic.trading
/**
 * Calculator logic for Trading Margin.
 * Calculates the margin required to take a position based on leverage.
 */
object MarginCalculator {
    /**
     * Calculates Margin Required.
     *
     * @param quantity The number of shares/units.
     * @param price The price per share/unit.
     * @param leverage The leverage provided by the broker (default 5x).
     * @return A [Triple] containing:
     *  - First: Margin Required
     *  - Second: Total Exposure (Quantity * Price)
     *  - Third: Total Value (Same as Exposure)
     */
    fun calculate(quantity: Double, price: Double, leverage: Double = 5.0): Triple<Double, Double, Double> {
        val totalValue = quantity * price
        val marginRequired = totalValue / leverage
        val exposure = totalValue
        return Triple(marginRequired, exposure, totalValue)
    }
}
