package ru.rznnike.demokmp.app.di

import org.koin.dsl.module
import ru.rznnike.demokmp.data.gateway.*
import ru.rznnike.demokmp.domain.gateway.*

internal val gatewayModule = module {
    single<PreferencesGateway> { PreferencesGatewayImpl(get(), get()) }
    single<HTTPExampleGateway> { HTTPExampleGatewayImpl(get(), get()) }
    single<WebSocketExampleGateway> { WebSocketExampleGatewayImpl(get(), get()) }
    single<DBExampleGateway> { DBExampleGatewayImpl(get(), get(), get()) }
    single<PdfExampleGateway> { PdfExampleGatewayImpl(get()) }
}