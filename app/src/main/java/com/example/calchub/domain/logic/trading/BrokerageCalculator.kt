package com.example.calchub.domain.logic.trading
/**
 * Calculator logic for Stock Brokerage.
 * Calculates the total brokerage and taxes for equity delivery or intraday trades.
 */
object BrokerageCalculator {
    /**
     * Calculates Brokerage and Other Charges.
     *
     * @param tradeValue The total value of the trade.
     * @param brokerageRate The brokerage rate in percent (default 0.03%).
     * @param isIntraday True if the trade is Intraday, False for Delivery.
     * @return A [Triple] containing:
     *  - First: Trade Value
     *  - Second: Total Charges (Brokerage + STT + etc.)
     *  - Third: Net Value (Trade Value - Charges)
     */
    fun calculate(tradeValue: Double, brokerageRate: Double = 0.03, isIntraday: Boolean = false): Triple<Double, Double, Double> {
        val brokerage = tradeValue * (brokerageRate / 100)
        val stt = if (isIntraday) tradeValue * 0.00025 else tradeValue * 0.001
        val totalCharges = brokerage + stt
        return Triple(tradeValue, totalCharges, tradeValue - totalCharges)
    }
}
