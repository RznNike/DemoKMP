package ru.rznnike.demokmp.data.network

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import ru.rznnike.demokmp.data.network.model.RandomImageLinksModel
import ru.rznnike.demokmp.data.utils.json.defaultJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private const val BASE_URL = "https://test.local/"
private const val IMAGES_URL = "https://test.local/api/breeds/image/random/3"
private const val IMAGES_JSON = """{"message":["a","b","c"]}"""

private fun createApi(
    body: String,
    onRequest: (HttpRequestData) -> Unit = {}
): AppApi = Ktorfit
    .Builder()
    .baseUrl(BASE_URL)
    .httpClient(
        HttpClient(
            MockEngine { request ->
                onRequest(request)
                respond(
                    content = body,
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }
        ) {
            install(ContentNegotiation) {
                json(defaultJson)
            }
        }
    )
    .build()
    .createAppApi()

/** Ktor wraps decoder failures in `JsonConvertException`, so match the wrapped cause. */
private fun assertSerializationFailure(error: Throwable) {
    assertTrue(
        actual = generateSequence(error) { it.cause }.any { it is SerializationException },
        message = "expected a SerializationException in the cause chain, got $error"
    )
}

/**
 * Drives the generated Ktorfit implementation with `MockEngine`: only the transport is replaced,
 * path templating and `defaultJson` + ContentNegotiation stay the production wiring from
 * `NetworkModule`.
 *
 * Transport-level error mapping is intentionally absent: `HttpErrorResponseInterceptor` is an
 * OkHttp interceptor installed through `configureOkHttp { addInterceptor(...) }`, so `MockEngine`
 * never runs it. That mapping is pinned by `HttpErrorResponseInterceptorTest`.
 */
class AppApiTest {
    @Test
    fun `getRandomImages expands the count path segment`() = runTest {
        // Arrange
        val requests = mutableListOf<HttpRequestData>()
        val api = createApi(IMAGES_JSON) { requests += it }

        // Act
        api.getRandomImages(3)

        // Assert
        assertEquals(IMAGES_URL, requests.single().url.toString())
    }

    @Test
    fun `getRandomImages decodes message into links`() = runTest {
        // Arrange
        val api = createApi(IMAGES_JSON)

        // Act
        val result = api.getRandomImages(3)

        // Assert
        assertEquals(RandomImageLinksModel(links = listOf("a", "b", "c")), result)
    }

    @Test
    fun `getRandomImages tolerates unknown fields`() = runTest {
        // Arrange
        val api = createApi("""{"message":["a"],"status":"success","extra":{"code":1}}""")

        // Act
        val result = api.getRandomImages(1)

        // Assert
        assertEquals(RandomImageLinksModel(links = listOf("a")), result)
    }

    @Test
    fun `getRandomImages fails when message is missing`() = runTest {
        // Arrange
        val api = createApi("""{"status":"success"}""")

        // Act
        val error = assertFailsWith<Exception> { api.getRandomImages(1) }

        // Assert
        assertSerializationFailure(error)
    }

    @Test
    fun `getRandomImages fails on malformed json`() = runTest {
        // Arrange (unterminated array)
        val api = createApi("{\"message\":[\"a\"")

        // Act
        val error = assertFailsWith<Exception> { api.getRandomImages(1) }

        // Assert
        assertSerializationFailure(error)
    }
}
