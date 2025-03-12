package ru.rznnike.demokmp.domain.log

enum class NetworkRequestState {
    SENT,
    SUCCESS,
    ERROR,
    TIMEOUT,
    CANCELLED
}