package com.qless.domain.usecase

import com.qless.domain.model.AuthUser
import com.qless.domain.repository.UserRepository

class LoginUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(email: String, password: String, rememberMe: Boolean): Result<AuthUser> =
        repository.login(email, password, rememberMe)
}

class RegisterUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<Unit> =
        repository.register(name, email, password)
}

class LogoutUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.logout()
}

class RestoreSessionUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): Result<AuthUser?> = repository.tryRestoreSession()
}

class ClearSessionUseCase(private val repository: UserRepository) {
    suspend operator fun invoke() = repository.clearSession()
}

class ToggleFavoritoUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(localId: String, currentFavoritos: List<String>): Result<List<String>> =
        repository.toggleFavorito(localId, currentFavoritos)
}

class DeleteAccountUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(email: String): Result<Unit> = repository.deleteAccount(email)
}

class ConsumeFirstOrderDiscountUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.consumeFirstOrderDiscount()
}
