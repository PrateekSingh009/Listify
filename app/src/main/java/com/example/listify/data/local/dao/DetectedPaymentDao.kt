package com.example.listify.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.listify.data.local.entity.DetectedPaymentEntity
import com.example.listify.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetectedPaymentDao {

    @Insert
    suspend fun insert(payment: DetectedPaymentEntity)


    @Query("""
        SELECT * FROM detected_payments
        WHERE isProcessed = 0 
        ORDER BY timestamp DESC
        LIMIT 1
    """)
    fun getLatestActivePayment(): Flow<List<DetectedPaymentEntity>>

    @Query("SELECT * FROM detected_payments WHERE isProcessed = 0 ORDER BY timestamp DESC")
    fun getUnprocessedPayments(): Flow<List<DetectedPaymentEntity>>

    @Query("""
        SELECT * FROM detected_payments
        WHERE isProcessed = 1
        ORDER BY timestamp DESC
    """)
    fun getProcessedPayments(): Flow<List<DetectedPaymentEntity>>

    @Query("UPDATE detected_payments SET isProcessed = 1 WHERE id = :id")
    suspend fun markAsProcessed(id: Long)

    @Update
    suspend fun update(payment: DetectedPaymentEntity)

    @Delete
    suspend fun deleteDetectedPayment(detectedPaymentEntity: DetectedPaymentEntity)
}