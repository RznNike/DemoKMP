package ru.rznnike.demokmp.domain.interactor.preferences

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakePreferencesGateway
import ru.rznnike.demokmp.domain.model.common.UiScale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SetUiScaleUseCaseTest {
    @Test
    fun `forwards the ui scale to the preferences gateway`() = runTest {
        // Arrange
        val gateway = FakePreferencesGateway()
        val useCase = SetUiScaleUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(UiScale.S130)

        // Assert
        assertEquals(listOf(UiScale.S130), gateway.setUiScaleValues)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of forwarding the ui scale`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakePreferencesGateway().apply { errorToThrow = expected }
        val useCase = SetUiScaleUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(UiScale.S130)

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
        assertTrue(gateway.setUiScaleValues.isEmpty())
    }
}
