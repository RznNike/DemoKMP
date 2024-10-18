package ru.rznnike.demokmp.data.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import ru.rznnike.demokmp.data.network.error.CustomServerException
import ru.rznnike.demokmp.data.utils.HttpCode

class HttpErrorResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain
            .proceed(chain.request())
            .run {
                if (code == HttpCode.NO_CONTENT.code) {
                    newBuilder()
                        .code(HttpCode.CUSTOM_NO_CONTENT.code)
                        .build()
                } else {
                    this
                }
            }
        if (!response.isSuccessful) {
            throw CustomServerException(
                httpCode = response.code
            )
        }
        return response
    }
}
