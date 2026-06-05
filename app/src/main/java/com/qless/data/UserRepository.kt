package com.qless.data

import com.qless.data.local.dao.UserDao
import com.qless.data.local.entity.UserEntity
import java.security.MessageDigest

class UserRepository(private val dao: UserDao) {

    suspend fun register(name: String, email: String, password: String): Result<Unit> {
        if (dao.findByEmail(email.lowercase()) != null) {
            return Result.failure(Exception("El correo ya esta registrado"))
        }
        dao.insert(
            UserEntity(
                email = email.lowercase(),
                name = name,
                passwordHash = hash(password),
                role = "USER",
            )
        )
        return Result.success(Unit)
    }

    suspend fun login(email: String, password: String): UserEntity? {
        val user = dao.findByEmail(email.lowercase()) ?: return null
        return if (user.passwordHash == hash(password)) user else null
    }

    suspend fun deleteAccount(email: String) = dao.deleteByEmail(email.lowercase())

    suspend fun seedBackOffice() {
        if (dao.findByEmail("backoffice@gmail.com") == null) {
            dao.insert(
                UserEntity(
                    email = "backoffice@gmail.com",
                    name = "Back Office",
                    passwordHash = hash("back office"),
                    role = "BACK_OFFICE",
                )
            )
        }
    }

    private fun hash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
