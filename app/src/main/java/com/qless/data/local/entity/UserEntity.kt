package com.qless.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qless.data.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val name: String,
    val passwordHash: String,
    val role: String,
)

fun UserEntity.toDomain() = User(
    email = email,
    name = name,
    role = role,
)
