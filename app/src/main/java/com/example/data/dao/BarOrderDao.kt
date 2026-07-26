package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.BarOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BarOrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: BarOrderEntity): Long

    @Query("SELECT * FROM bar_orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<BarOrderEntity>>

    @Query("SELECT COUNT(*) FROM bar_orders")
    fun getOrderCount(): Flow<Int>

    @Query("DELETE FROM bar_orders")
    suspend fun clearHistory()
}
