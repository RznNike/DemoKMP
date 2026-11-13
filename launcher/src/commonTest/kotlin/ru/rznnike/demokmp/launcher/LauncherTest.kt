package ru.rznnike.demokmp.launcher

import kotlin.test.Test
import kotlin.test.assertEquals

class LauncherTest {
    @Test
    fun `buildLaunchArguments appends the render api argument`() {
        // Act
        val arguments = buildLaunchArguments(
            jarPath = "/opt/app/application/app.jar",
            renderApi = "vulkan"
        )

        // Assert
        assertEquals(
            listOf("-jar", "/opt/app/application/app.jar", "renderApi=vulkan"),
            arguments
        )
    }

    @Test
    fun `buildLaunchArguments keeps an empty render api argument`() {
        // Act
        val arguments = buildLaunchArguments(
            jarPath = "C:\\App\\application\\app.jar",
            renderApi = ""
        )

        // Assert
        assertEquals(
            listOf("-jar", "C:\\App\\application\\app.jar", "renderApi="),
            arguments
        )
    }

    @Test
    fun `formatJavaVersionError includes the detected version`() {
        assertEquals(
            "Java 17+ required for launch, current version - 11",
            formatJavaVersionError(11)
        )
    }

    @Test
    fun `formatJavaVersionError reports none when version is unknown`() {
        assertEquals(
            "Java 17+ required for launch, current version - none",
            formatJavaVersionError(null)
        )
    }
}
