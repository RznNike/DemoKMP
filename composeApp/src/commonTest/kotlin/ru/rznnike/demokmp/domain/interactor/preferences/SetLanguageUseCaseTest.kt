package ru.rznnike.demokmp.domain.interactor.preferences

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakePreferencesGateway
import ru.rznnike.demokmp.domain.model.common.Language
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SetLanguageUseCaseTest {
    @Test
    fun `forwards the language to the preferences gateway`() = runTest {
        // Arrange
        val gateway = FakePreferencesGateway()
        val useCase = SetLanguageUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(Language.RU)

        // Assert
        assertEquals(listOf(Language.RU), gateway.setLanguageValues)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of forwarding the language`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakePreferencesGateway().apply { errorToThrow = expected }
        val useCase = SetLanguageUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(Language.RU)

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
        assertTrue(gateway.setLanguageValues.isEmpty())
    }
}
