package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavBackStack
import kotlin.test.Test
import kotlin.test.assertEquals

class FlowNavigatorTest {
    private val backStack = NavBackStack<NavigationScreen>()
    private val navigationStructure = mutableStateListOf<NavigationFlowInfo>()
    private var windowCloseRequests = 0

    private val navigator = FlowNavigator(
        backStack = backStack,
        navigationStructure = navigationStructure,
        closeWindowCallback = { windowCloseRequests += 1 }
    )

    private fun structureTypes() = navigationStructure.map { it.type }

    private fun screenIds() = backStack.map { (it as TestScreen).id }

    @Test
    fun `openFlow adds a structure entry and the flow screens`() {
        // Arrange
        val flow = FirstTestFlow(listOf(TestScreen("a1"), TestScreen("a2")))

        // Act
        navigator.openFlow(flow)

        // Assert
        assertEquals(listOf(FirstTestFlow::class.qualifiedName), structureTypes())
        assertEquals(listOf("a1", "a2"), screenIds())
        assertEquals(2, navigationStructure.single().screenCount)
    }

    @Test
    fun `openFlows adds every flow`() {
        // Arrange
        val flows = listOf(
            FirstTestFlow(listOf(TestScreen("a1"))),
            SecondTestFlow(listOf(TestScreen("b1"), TestScreen("b2")))
        )

        // Act
        navigator.openFlows(flows)

        // Assert
        assertEquals(
            listOf(FirstTestFlow::class.qualifiedName, SecondTestFlow::class.qualifiedName),
            structureTypes()
        )
        assertEquals(listOf("a1", "b1", "b2"), screenIds())
    }

    @Test
    fun `replaceFlow removes only the last flow screens`() {
        // Arrange
        navigator.openFlow(FirstTestFlow(listOf(TestScreen("a1"))))
        navigator.openFlow(SecondTestFlow(listOf(TestScreen("b1"), TestScreen("b2"))))

        // Act
        navigator.replaceFlow(ThirdTestFlow(listOf(TestScreen("c1"))))

        // Assert
        assertEquals(
            listOf(FirstTestFlow::class.qualifiedName, ThirdTestFlow::class.qualifiedName),
            structureTypes()
        )
        assertEquals(listOf("a1", "c1"), screenIds())
    }

    @Test
    fun `newRootFlow clears the previous flows and screens`() {
        // Arrange
        navigator.openFlow(FirstTestFlow(listOf(TestScreen("a1"))))
        navigator.openScreen(TestScreen("a2"))

        // Act
        navigator.newRootFlow(SecondTestFlow(listOf(TestScreen("b1"))))

        // Assert
        assertEquals(listOf(SecondTestFlow::class.qualifiedName), structureTypes())
        assertEquals(listOf("b1"), screenIds())
        assertEquals(1, navigationStructure.single().screenCount)
    }

    @Test
    fun `openFlowSingle replaces a matching flow at its offset`() {
        // Arrange
        navigator.openFlow(FirstTestFlow(listOf(TestScreen("a1"))))
        navigator.openFlow(SecondTestFlow(listOf(TestScreen("b1"), TestScreen("b2"))))

        // Act
        navigator.openFlowSingle(FirstTestFlow(listOf(TestScreen("a2"), TestScreen("a3"))))

        // Assert
        assertEquals(
            listOf(SecondTestFlow::class.qualifiedName, FirstTestFlow::class.qualifiedName),
            structureTypes()
        )
        assertEquals(listOf("b1", "b2", "a2", "a3"), screenIds())
    }

    @Test
    fun `openFlowSingle appends a flow when no matching type exists`() {
        // Arrange
        navigator.openFlow(FirstTestFlow(listOf(TestScreen("a1"))))

        // Act
        navigator.openFlowSingle(SecondTestFlow(listOf(TestScreen("b1"))))

        // Assert
        assertEquals(
            listOf(FirstTestFlow::class.qualifiedName, SecondTestFlow::class.qualifiedName),
            structureTypes()
        )
        assertEquals(listOf("a1", "b1"), screenIds())
    }

    @Test
    fun `closeFlow removes the last flow and its screens`() {
        // Arrange
        navigator.openFlow(FirstTestFlow(listOf(TestScreen("a1"), TestScreen("a2"))))
        navigator.openFlow(SecondTestFlow(listOf(TestScreen("b1"))))

        // Act
        navigator.closeFlow()

        // Assert
        assertEquals(listOf(FirstTestFlow::class.qualifiedName), structureTypes())
        assertEquals(listOf("a1", "a2"), screenIds())
        assertEquals(0, windowCloseRequests)
    }

    @Test
    fun `closeFlow on the last flow requests the window to close`() {
        // Arrange
        navigator.openFlow(FirstTestFlow(listOf(TestScreen("a1"))))

        // Act
        navigator.closeFlow()

        // Assert
        assertEquals(1, windowCloseRequests)
        assertEquals(listOf(FirstTestFlow::class.qualifiedName), structureTypes())
        assertEquals(listOf("a1"), screenIds())
    }

    @Test
    fun `openScreen increments only the last flow screen count`() {
        // Arrange
        navigator.openFlow(FirstTestFlow(listOf(TestScreen("a1"))))
        navigator.openFlow(SecondTestFlow(listOf(TestScreen("b1"))))

        // Act
        navigator.openScreen(TestScreen("b2"))

        // Assert
        assertEquals(1, navigationStructure[0].screenCount)
        assertEquals(2, navigationStructure[1].screenCount)
        assertEquals(listOf("a1", "b1", "b2"), screenIds())
    }

    @Test
    fun `openScreens increments the last flow screen count by the number of screens`() {
        // Arrange
        navigator.openFlow(FirstTestFlow(listOf(TestScreen("a1"))))

        // Act
        navigator.openScreens(listOf(TestScreen("a2"), TestScreen("a3")))

        // Assert
        assertEquals(3, navigationStructure.single().screenCount)
        assertEquals(listOf("a1", "a2", "a3"), screenIds())
    }

    @Test
    fun `replaceScreen keeps the screen count`() {
        // Arrange
        navigator.openFlow(FirstTestFlow(listOf(TestScreen("a1"), TestScreen("a2"))))

        // Act
        navigator.replaceScreen(TestScreen("a3"))

        // Assert
        assertEquals(2, navigationStructure.single().screenCount)
        assertEquals(listOf("a1", "a3"), screenIds())
    }

    @Test
    fun `newRootScreen resets the screen count to one`() {
        // Arrange
        navigator.openFlow(FirstTestFlow(listOf(TestScreen("a1"), TestScreen("a2"), TestScreen("a3"))))

        // Act
        navigator.newRootScreen(TestScreen("a4"))

        // Assert
        assertEquals(1, navigationStructure.single().screenCount)
        assertEquals(listOf("a4"), screenIds())
    }

    @Test
    fun `closeScreen removes the last screen and decrements the count`() {
        // Arrange
        navigator.openFlow(FirstTestFlow(listOf(TestScreen("a1"), TestScreen("a2"))))

        // Act
        navigator.closeScreen()

        // Assert
        assertEquals(1, navigationStructure.single().screenCount)
        assertEquals(listOf("a1"), screenIds())
        assertEquals(0, windowCloseRequests)
    }

    @Test
    fun `closeScreen on a single screen flow closes the flow`() {
        // Arrange
        navigator.openFlow(FirstTestFlow(listOf(TestScreen("a1"))))

        // Act
        navigator.closeScreen()

        // Assert
        assertEquals(1, windowCloseRequests)
    }
}
