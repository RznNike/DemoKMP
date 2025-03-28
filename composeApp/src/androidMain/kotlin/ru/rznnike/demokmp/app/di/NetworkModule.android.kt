package ru.rznnike.demokmp.app.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.Interceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.scope.Scope

private const val MAX_CONTENT_LENGTH = 500_000L // default is 250_000L

actual fun Scope.getPlatformHttpInterceptors(): List<Interceptor> = listOf(
    ChuckerInterceptor.Builder(androidApplication())
        .maxContentLength(MAX_CONTENT_LENGTH)
        .alwaysReadResponseBody(true)
        .build()
)