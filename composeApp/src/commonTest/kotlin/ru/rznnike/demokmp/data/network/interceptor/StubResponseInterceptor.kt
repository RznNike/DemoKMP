package ru.rznnike.demokmp.data.network.interceptor

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Terminal interceptor that returns a canned response without touching the network
 * and records the request it received.
 *
 * Registered last in the chain, so OkHttp's redirect, retry and authentication
 * machinery (which sits further down) never runs.
 */
internal class StubResponseInterceptor(
    var code: Int = 200,
    var body: String = "",
    var headers: Map<String, String> = emptyMap(),
) : Interceptor {
    var proceedCount: Int = 0
        private set

    var lastRequest: Request? = null
        private set

    override fun intercept(chain: Interceptor.Chain): Response {
        proceedCount++
        val request = chain.request()
        lastRequest = request
        val builder = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("stub")
            .body(body.toResponseBody(null))
        headers.forEach { (name, value) -> builder.header(name, value) }
        return builder.build()
    }
}
