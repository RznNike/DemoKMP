package ru.rznnike.demokmp.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleCollector
import kotlinx.serialization.modules.polymorphic
import java.io.File
import java.lang.reflect.Modifier
import java.util.jar.JarFile
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertTrue

private const val SCREEN_PACKAGE_PATH = "ru/rznnike/demokmp/app/ui/screen"

/**
 * `CreateNavDisplay` builds its `SavedStateConfiguration` from [screenKeyList] and hands
 * it to `rememberNavBackStack`, so a screen that can land on that back stack but is not
 * registered makes navigation state save/restore throw. Nothing else exercises this list.
 *
 * The screen set is discovered from the compiled output instead of being hard-coded, so
 * a newly added screen is picked up automatically. `NavigationScreen` is not sealed and
 * therefore has no `sealedSubclasses`, hence the classpath scan.
 */
class ScreenKeyListTest {
    @Test
    fun `every android screen is registered in screenKeyList`() {
        // Arrange
        val screens = scanScreenClasses()

        // Act
        val unregistered = findUnregistered(screens)

        // Assert
        assertTrue(
            screens.isNotEmpty(),
            "Classpath scan found no screens under $SCREEN_PACKAGE_PATH — the test would be vacuous"
        )
        assertTrue(
            unregistered.isEmpty(),
            "Screens missing from NavigationUtils.android.kt screenKeyList: " +
                unregistered.mapNotNull { it.simpleName }.sorted()
        )
    }
}

private fun findUnregistered(
    screens: List<KClass<out NavigationScreen>>
): List<KClass<out NavigationScreen>> {
    val serializersModule = SerializersModule {
        polymorphic(
            baseClass = NavKey::class,
            builderAction = screenKeyList
        )
    }
    val registered = mutableSetOf<KClass<*>>()
    serializersModule.dumpTo(object : SerializersModuleCollector {
        override fun <T : Any> contextual(
            kClass: KClass<T>,
            provider: (serializers: List<KSerializer<*>>) -> KSerializer<*>
        ) = Unit

        override fun <Base : Any, Sub : Base> polymorphic(
            baseClass: KClass<Base>,
            actualClass: KClass<Sub>,
            actualSerializer: KSerializer<Sub>
        ) {
            if (baseClass == NavKey::class) {
                registered += actualClass
            }
        }

        override fun <Base : Any> polymorphicDefaultSerializer(
            baseClass: KClass<Base>,
            defaultSerializerProvider: (value: Base) -> SerializationStrategy<Base>?
        ) = Unit

        override fun <Base : Any> polymorphicDefaultDeserializer(
            baseClass: KClass<Base>,
            defaultDeserializerProvider: (className: String?) -> DeserializationStrategy<Base>?
        ) = Unit
    })
    return screens.filterNot { it in registered }
}

/**
 * Finds every concrete `NavigationScreen` class compiled for this platform by walking
 * the test runtime classpath (both class directories and jars). Test classes live in a
 * different package, so they are not picked up.
 */
@Suppress("UNCHECKED_CAST")
private fun scanScreenClasses(): List<KClass<out NavigationScreen>> {
    val classLoader = NavigationScreen::class.java.classLoader ?: return emptyList()
    val classNames = linkedSetOf<String>()

    System.getProperty("java.class.path")
        .orEmpty()
        .split(File.pathSeparatorChar)
        .filter { it.isNotBlank() }
        .forEach { entry ->
            val root = File(entry)
            when {
                root.isDirectory -> {
                    val packageDir = File(root, SCREEN_PACKAGE_PATH)
                    if (packageDir.isDirectory) {
                        packageDir.walkTopDown()
                            .filter { it.isFile && it.extension == "class" }
                            .forEach { file ->
                                classNames += file.relativeTo(root).invariantSeparatorsPath
                                    .removeSuffix(".class")
                                    .replace('/', '.')
                            }
                    }
                }

                root.isFile && root.extension == "jar" -> JarFile(root).use { jar ->
                    jar.entries().asSequence()
                        .filter { !it.isDirectory }
                        .map { it.name }
                        .filter { it.startsWith("$SCREEN_PACKAGE_PATH/") && it.endsWith(".class") }
                        .forEach { name ->
                            classNames += name.removeSuffix(".class").replace('/', '.')
                        }
                }
            }
        }

    return classNames
        .filterNot { it.contains('$') }
        .map { Class.forName(it, false, classLoader) }
        .filter { screenClass ->
            NavigationScreen::class.java.isAssignableFrom(screenClass) &&
                !Modifier.isAbstract(screenClass.modifiers)
        }
        .map { it.kotlin as KClass<out NavigationScreen> }
}
