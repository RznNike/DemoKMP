package ru.rznnike.demokmp.domain.log

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class LoggerNotInitializedTest {
    private val originalErr = System.err
    private val errBuffer = ByteArrayOutputStream()

    @BeforeTest
    fun setUp() {
        // the logger is a global singleton: make sure no other test class left it initialized
        Logger.reset()
        errBuffer.reset()
        System.setErr(PrintStream(errBuffer, true))
    }

    @AfterTest
    fun tearDown() {
        System.setErr(originalErr)
    }

    @Test
    fun `logging before the logger is initialized reports the error to stderr`() {
        // Act
        Logger.i("message")

        // Assert
        assertTrue(errBuffer.toString().contains("Logger is not initialized!"))
    }

    @Test
    fun `the not initialized error is reported with the logger tag and the error level`() {
        // Act
        Logger.i("message")

        // Assert
        val output = errBuffer.toString()
        assertTrue(output.contains(" | E | Logger | Logger is not initialized!"))
    }
}
