package ru.rznnike.demokmp.domain.interactor.pdfexample

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakePdfExampleGateway
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetSamplePdfUseCaseTest {
    @Test
    fun `returns the sample pdf file provided by the gateway`() = runTest {
        // Arrange
        val expected = File("sample.pdf")
        val gateway = FakePdfExampleGateway().apply { samplePdf = expected }
        val useCase = GetSamplePdfUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakePdfExampleGateway().apply { errorToThrow = expected }
        val useCase = GetSamplePdfUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
