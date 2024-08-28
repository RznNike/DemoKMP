package ru.rznnike.demokmp.app.di

import org.koin.dsl.module
import ru.rznnike.demokmp.data.gateway.DBExampleGatewayImpl
import ru.rznnike.demokmp.data.gateway.HTTPExampleGatewayImpl
import ru.rznnike.demokmp.data.gateway.PreferencesGatewayImpl
import ru.rznnike.demokmp.data.gateway.WebSocketExampleGatewayImpl
import ru.rznnike.demokmp.domain.gateway.DBExampleGateway
import ru.rznnike.demokmp.domain.gateway.HTTPExampleGateway
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway
import ru.rznnike.demokmp.domain.gateway.WebSocketExampleGateway

internal val gatewayModule = module {
    single<PreferencesGateway> { PreferencesGatewayImpl(get(), get()) }
    single<HTTPExampleGateway> { HTTPExampleGatewayImpl(get(), get()) }
    single<WebSocketExampleGateway> { WebSocketExampleGatewayImpl(get(), get()) }
    single<DBExampleGateway> { DBExampleGatewayImpl(get(), get()) }
}