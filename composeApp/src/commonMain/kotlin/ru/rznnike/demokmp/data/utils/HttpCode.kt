package ru.rznnike.demokmp.data.utils

enum class HttpCode(val code: Int) {
    OK(200),
    NO_CONTENT(204),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    CONFLICT(409),
    INTERNAL_SERVER_ERROR(500),
    BAD_GATEWAY(502),
    SERVICE_UNAVAILABLE(503),
    CUSTOM_NO_CONTENT(2040);

    companion object {
        operator fun get(code: Int?) = entries.find { it.code == code } ?: OK
    }
}