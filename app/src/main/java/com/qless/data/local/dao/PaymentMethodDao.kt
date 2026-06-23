package com.qless.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qless.data.local.entity.PaymentMethodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentMethodDao {

    @Query("SELECT * FROM payment_methods")
    fun getAll(): Flow<List<PaymentMethodEntity>>

    @Query("SELECT COUNT(*) FROM payment_methods")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(method: PaymentMethodEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(methods: List<PaymentMethodEntity>)

    @Query("DELETE FROM payment_methods WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE payment_methods SET esPrincipal = 0")
    suspend fun clearPrincipal()
}
