package ru.rznnike.demokmp.data.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import ru.rznnike.demokmp.data.network.error.CustomServerException

class HttpErrorResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (!response.isSuccessful) {
            throw CustomServerException(
                httpCode = response.code
            )
        }
        return response
    }
}
