package ru.rznnike.demokmp.app.di

import org.koin.dsl.module
import ru.rznnike.demokmp.data.gateway.NetworkTestGatewayImpl
import ru.rznnike.demokmp.data.gateway.PreferencesGatewayImpl
import ru.rznnike.demokmp.domain.gateway.NetworkTestGateway
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway

internal val gatewayModule = module {
    single<PreferencesGateway> { PreferencesGatewayImpl(get(), get()) }
    single<NetworkTestGateway> { NetworkTestGatewayImpl(get(), get()) }
}