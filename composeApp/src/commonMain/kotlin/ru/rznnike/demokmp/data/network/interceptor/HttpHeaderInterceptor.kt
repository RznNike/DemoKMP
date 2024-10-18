package ru.rznnike.demokmp.data.network.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.rznnike.demokmp.data.preference.PreferencesManager
import ru.rznnike.demokmp.data.utils.DataConstants
import java.io.IOException

class HttpHeaderInterceptor(
    private val preferencesManager: PreferencesManager
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        fun Request.Builder.setHeader(name: String, value: String): Request.Builder {
            removeHeader(name)
            addHeader(name, value)
            return this
        }

//        val authToken: String
//        runBlocking {
//            authToken = preferencesManager.authToken.get()
//        }
        val request = chain.request()
            .newBuilder()
            .apply {
//                if (authToken.isNotBlank()) {
//                    setHeader(
//                        DataConstants.HEADER_AUTHORIZATION,
//                        DataConstants.BEARER_AUTH_TEMPLATE.format(authToken)
//                    )
//                }
                setHeader(
                    DataConstants.HEADER_TEST,
                    "test header content"
                )
            }
            .build()
        return chain.proceed(request)
    }
}
