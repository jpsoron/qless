package com.qless.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.qless.data.local.dao.CartItemDao
import com.qless.data.local.dao.LocalDao
import com.qless.data.local.dao.MenuItemDao
import com.qless.data.local.dao.PaymentMethodDao
import com.qless.data.local.dao.UserDao
import com.qless.data.local.entity.CartItemEntity
import com.qless.data.local.entity.LocalEntity
import com.qless.data.local.entity.MenuItemEntity
import com.qless.data.local.entity.PaymentMethodEntity
import com.qless.data.local.entity.UserEntity

@Database(
    entities = [
        CartItemEntity::class,
        PaymentMethodEntity::class,
        UserEntity::class,
        LocalEntity::class,
        MenuItemEntity::class,
    ],
    version = 6,
    exportSchema = false,
)
abstract class QLessDatabase : RoomDatabase() {

    abstract fun cartItemDao(): CartItemDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun userDao(): UserDao
    abstract fun localDao(): LocalDao
    abstract fun menuItemDao(): MenuItemDao

    companion object {
        @Volatile
        private var INSTANCE: QLessDatabase? = null

        fun getInstance(context: Context): QLessDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    QLessDatabase::class.java,
                    "qless_database",
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
            }
    }
}
