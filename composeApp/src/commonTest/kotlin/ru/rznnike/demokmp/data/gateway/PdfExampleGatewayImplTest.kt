package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.gateway.PdfExampleGateway
import java.io.File
import kotlin.test.*

class PdfExampleGatewayImplTest {
    private val gateway: PdfExampleGateway = PdfExampleGatewayImpl()

    // Same path resolution as production (relative to the test working directory)
    private val samplePdf = File(DataConstants.TEST_PDF_PATH)

    @BeforeTest
    fun setUp() {
        samplePdf.delete()
    }

    @AfterTest
    fun tearDown() {
        samplePdf.delete()
    }

    @Test
    fun `getSamplePdf returns existing file`() = runTest {
        // Arrange
        samplePdf.writeText("PDF sample content")

        // Act
        val result = gateway.getSamplePdf()

        // Assert
        assertTrue(result.exists())
        assertEquals(samplePdf.absolutePath, result.absolutePath)
    }

    @Test
    fun `getSamplePdf throws when file is missing`() = runTest {
        // Arrange
        assertFalse(samplePdf.exists())

        // Act & Assert
        assertFailsWith<NoSuchFileException> { gateway.getSamplePdf() }
    }
}
