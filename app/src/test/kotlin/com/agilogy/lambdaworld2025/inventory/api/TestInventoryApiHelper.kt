package com.agilogy.lambdaworld2025.inventory.api

import com.agilogy.lambdaworld2025.inventory.domain.InventoryLine
import com.agilogy.lambdaworld2025.inventory.domain.InventoryService
import com.agilogy.lambdaworld2025.product.domain.Product
import infrastructure.InMemoryInventoryRepository
import infrastructure.InMemoryProductsRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.time.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals

data class TestContext(
    val clock: Clock,
    val productsRepository: InMemoryProductsRepository,
    val inventoryRepository: InMemoryInventoryRepository,
    val httpClient: HttpClient,
) {
    suspend inline fun <reified A> post(
        uri: String,
        body: A,
    ) =
        httpClient.post(uri) {
            contentType(
                io.ktor.http.ContentType.Application.Json
            )
            setBody<A>(body)
        }

    suspend fun HttpResponse.assertSucceededWith(
        expectedStatus: HttpStatusCode,
        response: ReconcileStockResponse,
        finalState: Set<InventoryLine>,
    ) {
        assertEquals(
            Result(expectedStatus, response, finalState),
            Result(
                status,
                body<ReconcileStockResponse>(),
                inventoryRepository.state,
            ),
        )
    }

    suspend fun HttpResponse.assertFailedWith(
        expectedStatus: HttpStatusCode,
        expectedResponse: JsonObject,
        finalState: Set<InventoryLine>,
    ) {
        assertEquals(
            ErrorResult(
                expectedStatus,
                expectedResponse.toString(),
                finalState,
            ),
            ErrorResult(
                status,
                body<String>().format(),
                inventoryRepository.state,
            ),
        )
    }
}

private data class Result(
    val status: HttpStatusCode,
    val response: ReconcileStockResponse,
    val finalState: Set<InventoryLine>,
) {
    override fun toString(): String =
        "Ressult(\n  status=$status,\n  response=$response,\n  finalState=$finalState\n)"
}

private data class ErrorResult(
    val status: HttpStatusCode,
    val response: String?,
    val finalState: Set<InventoryLine>,
) {
    override fun toString(): String =
        "Ressult(\n  status=$status,\n  response=$response,\n  finalState=$finalState\n)"
}

private fun String.format() =
    runCatching { Json.parseToJsonElement(this).toString() }
        .getOrElse { this }
        .ifEmpty { null }

fun testInventoryApi(
    initialProducts: Set<Product>,
    initialInventory: Set<InventoryLine> = emptySet(),
    block: suspend TestContext.() -> Unit,
) = testApplication {
    val fixedInstant = Clock.System.now()
    val testClock =
        object : Clock {
            override fun now() = fixedInstant
        }
    val productsRepository =
        InMemoryProductsRepository(initialProducts)
    val inventoryRepository =
        InMemoryInventoryRepository(initialInventory)
    val inventoryService =
        InventoryService(
            testClock,
            productsRepository,
            inventoryRepository,
        )
    val inventoryApiServer =
        InventoryApiServer(inventoryService)
    val client = createClient {
        install(ContentNegotiation) { json() }
    }
    application { with(inventoryApiServer) { module() } }
    TestContext(
            testClock,
            productsRepository,
            inventoryRepository,
            client,
        )
        .block()
}
