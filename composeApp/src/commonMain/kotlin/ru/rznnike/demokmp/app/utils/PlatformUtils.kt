package ru.rznnike.demokmp.app.utils

import org.koin.core.component.KoinComponent

expect val platformName: String

expect fun getMacAddress(): String?

expect fun KoinComponent.openLink(link: String)

expect fun initCOMLibrary()

expect fun destroyCOMLibrary()