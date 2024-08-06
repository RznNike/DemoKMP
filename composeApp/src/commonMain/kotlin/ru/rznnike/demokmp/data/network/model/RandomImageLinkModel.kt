package ru.rznnike.demokmp.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RandomImageLinkModel(
    @SerialName("message")
    val link: String
)