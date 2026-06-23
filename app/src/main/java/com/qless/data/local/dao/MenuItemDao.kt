package com.qless.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.qless.data.local.entity.MenuItemEntity

@Dao
interface MenuItemDao {

    @Query("SELECT * FROM menu_items WHERE localId = :localId ORDER BY orden")
    suspend fun getForLocal(localId: String): List<MenuItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MenuItemEntity>)

    @Query("DELETE FROM menu_items WHERE localId = :localId")
    suspend fun deleteForLocal(localId: String)

    /** Refresca solo la carta de un local sin tocar la de los demás. */
    @Transaction
    suspend fun replaceForLocal(localId: String, items: List<MenuItemEntity>) {
        deleteForLocal(localId)
        insertAll(items)
    }
}
