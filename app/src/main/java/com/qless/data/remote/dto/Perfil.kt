package com.qless.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Perfil(
    val id: String,
    val nombre: String,
    val email: String,
    val rol: String,
    @SerialName("favoritos") val favoritos: List<String> = emptyList()
)
