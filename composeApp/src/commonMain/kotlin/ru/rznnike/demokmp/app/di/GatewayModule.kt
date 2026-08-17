package ru.rznnike.demokmp.app.di

import org.koin.dsl.module
import ru.rznnike.demokmp.data.gateway.*
import ru.rznnike.demokmp.domain.gateway.*

internal val gatewayModule = module {
    single<AppGateway> { AppGatewayImpl(get()) }
    single<PreferencesGateway> { PreferencesGatewayImpl(get()) }
    single<HTTPExampleGateway> { HTTPExampleGatewayImpl(get()) }
    single<WebSocketExampleGateway> { WebSocketExampleGatewayImpl(get()) }
    single<DBExampleGateway> { DBExampleGatewayImpl(get(), get()) }
    single<PdfExampleGateway> { PdfExampleGatewayImpl() }
    single<ChartExampleGateway> { ChartExampleGatewayImpl() }
    single<LogGateway> { LogGatewayImpl(get(), get(), get()) }
    single<ComObjectExampleGateway> { ComObjectExampleGatewayImpl(get()) }
    single<FileGateway> { FileGatewayImpl() }
}