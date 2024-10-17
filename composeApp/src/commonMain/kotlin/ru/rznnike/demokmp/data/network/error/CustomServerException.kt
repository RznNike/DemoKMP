package ru.rznnike.demokmp.data.network.error

import java.io.IOException

class CustomServerException(
    val httpCode: Int
) : IOException() {
    val isServerSide: Boolean = httpCode >= 500
}