package ru.rznnike.demokmp.app.di

import okhttp3.Interceptor
import org.koin.core.scope.Scope

actual fun Scope.getPlatformHttpInterceptors(): List<Interceptor> = emptyList()