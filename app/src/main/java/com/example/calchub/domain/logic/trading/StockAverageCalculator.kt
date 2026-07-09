package com.example.calchub.domain.logic.trading
/**
 * Calculator logic for Stock Price Averaging.
 * Calculates the average price of a stock when bought at different prices.
 */
object StockAverageCalculator {
    /**
     * Calculates Average Stock Price.
     *
     * @param firstQuantity Quantity bought in first tranche.
     * @param firstPrice Price paid in first tranche.
     * @param secondQuantity Quantity bought in second tranche.
     * @param secondPrice Price paid in second tranche.
     * @return A [Triple] containing:
     *  - First: Total Quantity
     *  - Second: Average Price
     *  - Third: Total Investment Amount
     */
    fun calculate(firstQuantity: Double, firstPrice: Double, secondQuantity: Double, secondPrice: Double): Triple<Double, Double, Double> {
        val totalQuantity = firstQuantity + secondQuantity
        val totalInvestment = (firstQuantity * firstPrice) + (secondQuantity * secondPrice)
        val averagePrice = totalInvestment / totalQuantity
        return Triple(totalQuantity, averagePrice, totalInvestment)
    }
}
