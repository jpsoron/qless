package com.qless.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.qless.data.local.entity.LocalEntity

@Dao
interface LocalDao {

    @Query("SELECT * FROM locales ORDER BY nombre")
    suspend fun getAll(): List<LocalEntity>

    @Query("SELECT * FROM locales WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<LocalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(locales: List<LocalEntity>)

    @Query("DELETE FROM locales")
    suspend fun deleteAll()

    /** Sincronización completa del catálogo: pisa la caché con lo último de la red. */
    @Transaction
    suspend fun replaceAll(locales: List<LocalEntity>) {
        deleteAll()
        upsertAll(locales)
    }
}
