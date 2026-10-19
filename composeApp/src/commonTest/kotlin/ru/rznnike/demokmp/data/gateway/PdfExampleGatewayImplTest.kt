package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.gateway.PdfExampleGateway
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

    @Test
    fun `savePdfToFile copies pdf content to target file`() = runTest {
        // Arrange
        val source = File.createTempFile("demokmp-source", ".pdf")
        val target = File.createTempFile("demokmp-target", ".pdf")
        try {
            val content = "PDF sample content".toByteArray()
            source.writeBytes(content)

            // Act
            gateway.savePdfToFile(tempPdfFile = source, saveFile = target)

            // Assert
            assertContentEquals(content, target.readBytes())
        } finally {
            source.delete()
            target.delete()
        }
    }

    @Test
    fun `savePdfToFile overwrites existing target file`() = runTest {
        // Arrange
        val source = File.createTempFile("demokmp-source", ".pdf")
        val target = File.createTempFile("demokmp-target", ".pdf")
        try {
            val content = "PDF new content".toByteArray()
            source.writeBytes(content)
            target.writeBytes("stale content".toByteArray())

            // Act
            gateway.savePdfToFile(tempPdfFile = source, saveFile = target)

            // Assert
            assertContentEquals(content, target.readBytes())
        } finally {
            source.delete()
            target.delete()
        }
    }
}
