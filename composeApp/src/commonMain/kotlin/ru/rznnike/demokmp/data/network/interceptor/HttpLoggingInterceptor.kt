package ru.rznnike.demokmp.data.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import ru.rznnike.demokmp.data.utils.HttpLogLevel
import ru.rznnike.demokmp.data.utils.getBufferedBody
import ru.rznnike.demokmp.data.utils.toLog
import ru.rznnike.demokmp.domain.log.Logger
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import java.io.IOException
import java.net.SocketException

class HttpLoggingInterceptor(
    val logLevel: HttpLogLevel = HttpLogLevel.BODY,
    val errorLogLevel: HttpLogLevel = HttpLogLevel.BODY
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (logLevel == HttpLogLevel.NONE) {
            return chain.proceed(request)
        }

        val logRequestUuid = Logger.networkRequest(
            message = request.toLog(
                level = logLevel
            ),
            curl = request.toCurl()
        )

        val startMs = System.currentTimeMillis()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            val state = when {
                (e is IOException) && (e.message == "Canceled") -> NetworkRequestState.TIMEOUT
                (e is SocketException) && (e.message == "Socket closed") -> NetworkRequestState.CANCELLED
                else -> NetworkRequestState.ERROR
            }
            Logger.networkResponse(
                requestUuid = logRequestUuid,
                message = "<-- HTTP FAILED: $e",
                state = state
            )
            throw e
        }

        val responseLogLevel = if (response.isSuccessful) logLevel else errorLogLevel
        val body = if (responseLogLevel == HttpLogLevel.BODY) response.getBufferedBody() else null
        val durationMs = System.currentTimeMillis() - startMs
        Logger.networkResponse(
            requestUuid = logRequestUuid,
            message = response.toLog(
                level = responseLogLevel,
                bufferedBody = body,
                durationMs = durationMs
            ),
            state = if (response.isSuccessful) NetworkRequestState.SUCCESS else NetworkRequestState.ERROR
        )

        return response
    }
}
