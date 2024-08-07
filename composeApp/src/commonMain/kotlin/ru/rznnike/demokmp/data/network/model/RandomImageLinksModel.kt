package ru.rznnike.demokmp.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RandomImageLinksModel(
    @SerialName("message")
    val links: List<String>
)