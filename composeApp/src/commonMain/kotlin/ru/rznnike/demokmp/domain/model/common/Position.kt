package ru.rznnike.demokmp.domain.model.common

import kotlinx.serialization.Serializable

@Serializable
data class Position(
    val lat: Double = 0.0,
    val lng: Double = 0.0
)