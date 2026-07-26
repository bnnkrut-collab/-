package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bar_orders")
data class BarOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderCode: String,
    val drinkName: String,
    val volumeMl: Int,
    val totalCalculationSum: Double,
    val timestamp: Long = System.currentTimeMillis()
)
