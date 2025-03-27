package ru.rznnike.demokmp.app.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.core.scope.Scope
import org.koin.dsl.module
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.data.network.AppWebSocketManager
import ru.rznnike.demokmp.data.network.createAppApi
import ru.rznnike.demokmp.data.network.interceptor.HttpBaseUrlInterceptor
import ru.rznnike.demokmp.data.network.interceptor.HttpErrorResponseInterceptor
import ru.rznnike.demokmp.data.network.interceptor.HttpHeaderInterceptor
import ru.rznnike.demokmp.data.network.interceptor.HttpLoggingInterceptor
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.data.utils.defaultJson
import java.io.File
import java.time.Duration
import java.util.concurrent.TimeUnit

private const val CACHE_MAX_SIZE = 100 * 1024 * 1024 // 100 MB
private const val TIMEOUT_MS = 60_000L // 60 sec
private const val WEB_SOCKET_PING_MS = 10_000L // 10 sec

internal val networkModule = module {
    fun getCache() = try {
        val cacheDirectory = File(DataConstants.NETWORK_CACHE_PATH)
        Cache(cacheDirectory, CACHE_MAX_SIZE.toLong())
    } catch (ignored: Exception) { null }

    fun OkHttpClient.Builder.allTimeoutsMs(timeout: Long) =
        this.callTimeout(timeout, TimeUnit.MILLISECONDS)
            .connectTimeout(timeout, TimeUnit.MILLISECONDS)
            .readTimeout(timeout, TimeUnit.MILLISECONDS)
            .writeTimeout(timeout, TimeUnit.MILLISECONDS)

    fun HttpClientConfig<OkHttpConfig>.configureOkHttp(
        additionalConfiguration: OkHttpClient.Builder.() -> Unit = {}
    ) {
        engine {
            config {
                additionalConfiguration()
                cache(getCache())
                allTimeoutsMs(TIMEOUT_MS)
            }
        }
    }

    fun HttpClientConfig<OkHttpConfig>.installJson() {
        install(ContentNegotiation) {
            json(defaultJson())
        }
        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }

    fun OkHttpClient.Builder.addLoggingInterceptor() {
        if (BuildKonfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor())
        }
    }

    single {
        Ktorfit.Builder()
            .baseUrl(BuildKonfig.API_MAIN)
            .httpClient(
                HttpClient(OkHttp) {
                    configureOkHttp {
                        addInterceptor(HttpBaseUrlInterceptor(get()))
                        addInterceptor(HttpHeaderInterceptor(get()))
                        addInterceptor(HttpErrorResponseInterceptor())
                        addLoggingInterceptor()
                        getPlatformHttpInterceptors().forEach {
                            addInterceptor(it)
                        }
                    }
                    installJson()
                }
            )
            .build()
            .createAppApi()
    }

    single {
        AppWebSocketManager(
            client = HttpClient(OkHttp) {
                configureOkHttp {
                    pingInterval(Duration.ofMillis(WEB_SOCKET_PING_MS))
                    addLoggingInterceptor()
                }
                install(WebSockets)
            },
            preferencesManager = get()
        )
    }
}

expect fun Scope.getPlatformHttpInterceptors(): List<Interceptor>