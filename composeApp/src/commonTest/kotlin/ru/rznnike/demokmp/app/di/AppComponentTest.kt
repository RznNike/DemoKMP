package ru.rznnike.demokmp.app.di

import kotlin.test.Test
import kotlin.test.assertNull
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class AppComponentTest {
    @Test
    fun `appComponent has a definition for every constructor dependency`() {
        // Arrange (verify() builds its index from the module's included modules, so the
        // seven appComponent modules must be folded into one for cross-module resolution)
        val aggregate = module { includes(appComponent) }

        // Act (verify() only inspects the definitions — nothing is instantiated)
        val failure = runCatching { aggregate.verify() }.exceptionOrNull()

        // Assert
        assertNull(failure, failure?.message)
    }
}
