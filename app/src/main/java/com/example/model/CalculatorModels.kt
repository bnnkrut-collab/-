package com.example.model

data class CalculatorState(
    val displayExpression: String = "",
    val currentResult: String = "0",
    val lastExpression: String = "",
    val error: String? = null
)

data class ChekushkaState(
    val levelMl: Int = 0,               // 0 to 250 мл
    val isCapClosed: Boolean = false,   // Bottle cap closed/open
    val totalDrunkMl: Int = 0,          // Total drunk counter
    val accumulatedSum: Double = 0.0,   // Accumulated calculation sum toward bar order
    val barTargetSum: Double = 500.0,   // Target limit in ₽ to auto-order in bar
    val totalBarOrders: Int = 0,
    val tauntMessage: String? = null    // Toast/Banner message from capping/drinking
)

data class BarOrderInfo(
    val orderCode: String,
    val drinkName: String,
    val volumeMl: Int,
    val totalSum: Double,
    val timestamp: Long
)
