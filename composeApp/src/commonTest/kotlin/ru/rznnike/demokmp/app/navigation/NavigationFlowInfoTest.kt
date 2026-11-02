package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.SaverScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private val saverScope = SaverScope { true }

class NavigationFlowInfoTest {
    @Test
    fun `listSaver saves one entry per flow`() {
        // Arrange
        val source = mutableStateListOf(
            NavigationFlowInfo("first", 2),
            NavigationFlowInfo("second", 1)
        )

        // Act
        val saved = with(NavigationFlowInfo.listSaver) { saverScope.save(source) }

        // Assert
        assertNotNull(saved)
        assertEquals(2, (saved as List<*>).size)
    }

    @Test
    fun `listSaver restores the saved entries`() {
        // Arrange
        val source = mutableStateListOf(
            NavigationFlowInfo("first", 2),
            NavigationFlowInfo("second", 1)
        )
        val saver = NavigationFlowInfo.listSaver
        val saved = with(saver) { saverScope.save(source) }

        // Act
        val restored = saver.restore(requireNotNull(saved))

        // Assert
        assertEquals(
            listOf("first" to 2, "second" to 1),
            restored?.map { it.type to it.screenCount }
        )
    }

    @Test
    fun `listSaver skips corrupt entries on restore`() {
        // Arrange
        val saver = NavigationFlowInfo.listSaver
        val saved = listOf("""{"type":"first","screenCount":2}""", "not json")

        // Act
        val restored = saver.restore(saved)

        // Assert
        assertEquals(
            listOf("first" to 2),
            restored?.map { it.type to it.screenCount }
        )
    }

    @Test
    fun `toInfo uses the qualified flow name and screen count`() {
        // Arrange
        val flow = FirstTestFlow(listOf(TestScreen("a1"), TestScreen("a2")))

        // Act
        val info = flow.toInfo()

        // Assert
        assertEquals(FirstTestFlow::class.qualifiedName, info.type)
        assertEquals(2, info.screenCount)
    }
}
