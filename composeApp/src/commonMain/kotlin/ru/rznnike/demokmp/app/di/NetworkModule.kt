package ru.rznnike.demokmp.app.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.dsl.module
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.data.network.createAppApi
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import java.io.File
import java.util.concurrent.TimeUnit

private const val RESPONSES_PATH = "${GlobalConstants.APP_DATA_DIR}/responses"
private const val CACHE_MAX_SIZE = 100 * 1024 * 1024 // 100 MB
private const val TIMEOUT_MS = 60_000L // 60 sec

internal val networkModule = module {
    fun getCache() = try {
        val httpCacheDirectory = File(RESPONSES_PATH)
        Cache(httpCacheDirectory, CACHE_MAX_SIZE.toLong())
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
            json(
                Json {
                    isLenient = true
                    explicitNulls = false
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    fun HttpClientConfig<OkHttpConfig>.installLogging() {
        if (BuildKonfig.DEBUG) {
            install(Logging) {
                level = LogLevel.BODY
            }
        }
    }

    single {
        Ktorfit.Builder()
            .baseUrl(BuildKonfig.API_MAIN)
            .httpClient(
                HttpClient(OkHttp) {
                    configureOkHttp()
                    installJson()
                    installLogging()
                }
            )
            .build()
            .createAppApi()
    }
}