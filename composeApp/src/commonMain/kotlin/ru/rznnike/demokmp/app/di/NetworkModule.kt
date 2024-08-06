package ru.rznnike.demokmp.app.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.data.network.createAppApi

internal val networkModule = module {
    single {
        Ktorfit
            .Builder()
            .baseUrl(BuildKonfig.API_MAIN)
            .httpClient(
                HttpClient() {
                    install(ContentNegotiation) {
                        json(Json { isLenient = true; ignoreUnknownKeys = true })
                    }
                }
            )
            .build()
            .createAppApi()
    }
}