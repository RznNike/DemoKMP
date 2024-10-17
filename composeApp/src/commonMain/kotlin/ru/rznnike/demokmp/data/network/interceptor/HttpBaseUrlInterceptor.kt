package ru.rznnike.demokmp.data.network.interceptor

import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.data.preference.PreferencesManager
import java.io.IOException

class HttpBaseUrlInterceptor(
    private val preferencesManager: PreferencesManager
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val baseUrl = runBlocking {
//            preferencesManager.httpServerUrl.get()
            BuildKonfig.API_MAIN
        }
        baseUrl.toHttpUrlOrNull()?.let {
            val newUrlBuilder: HttpUrl.Builder = it.newBuilder()
            for (segment in request.url.pathSegments) {
                newUrlBuilder.addPathSegment(segment)
            }
            newUrlBuilder.query(request.url.query)
            request = request.newBuilder()
                .url(newUrlBuilder.build())
                .build()
        }
        return chain.proceed(request)
    }
}
