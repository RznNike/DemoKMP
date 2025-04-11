package ru.rznnike.demokmp.app.di

val appComponent = listOf(
    appModule,
    getPreferenceModule(),
    gatewayModule,
    interactorModule,
    networkModule,
    getDatabaseModule()
)