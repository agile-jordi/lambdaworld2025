package com.agilogy.lambdaworld2025.inventory.api

import arrow.core.Either
import com.agilogy.lambdaworld2025.inventory.domain.InventoryLine
import com.agilogy.lambdaworld2025.inventory.domain.InventoryService
import com.agilogy.lambdaworld2025.json.json
import com.agilogy.lambdaworld2025.json.jsonArray
import com.agilogy.lambdaworld2025.json.jsonObject
import com.agilogy.lambdaworld2025.product.domain.Product
import com.agilogy.lambdaworld2025.product.domain.ProductsRepository
import infrastructure.InMemoryInventoryRepository
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

const val productId = "45678964"
const val productSku = "abc-123-1"
val product = Product(id = "45678964", sku = "abc-123-1")

class InventoryApiServerTest {

    @Test
    @DisplayName(
        "reconcile stock of product sku:$productSku, id:$productId to 23"
    )
    fun registerReconciledStock() {
        val products = setOf(product)
        testInventoryApi(products) {
            val stock = 23
            val response =
                post(
                    "/reconcileStock",
                    ReconcileStockRequest(
                        product.sku,
                        stock,
                    ),
                )
            response.assertSucceededWith(
                HttpStatusCode.OK,
                // "sku":"abc-123-1","stock":23,"reconciliationDate":1758887217682}
                ReconcileStockResponse(
                    productSku,
                    stock,
                    clock.now().toEpochMilliseconds(),
                ),
                setOf(
                    InventoryLine(
                            product.id,
                            stock,
                            clock.now(),
                        )
                        .getOrFail()
                ),
            )
        }
    }

    @Test
    @DisplayName(
        "fail to reconcile stock for product sku does-not-exist"
    )
    fun failForUnknownProductSku() {
        testInventoryApi(emptySet()) {
            val unknownSku = "does-not-exist"
            val response =
                post(
                    "/reconcileStock",
                    ReconcileStockRequest(unknownSku, 23),
                )
            response.assertFailedWith(
                HttpStatusCode.BadRequest,
                jsonObject(
                    "errors" to
                        jsonArray(
                            jsonObject(
                                "sku" to "not-found".json
                            )
                        )
                ),
                emptySet(),
            )
        }
    }

    @Test
    @DisplayName(
        "fail for reconciliation dates in the future"
    )
    fun failForReconciliationDatesInTheFuture() {
        val product =
            Product(id = "45678964", sku = "abc-123-1")
        val products = setOf(product)
        testInventoryApi(products) {
            val reconciliationDate =
                clock.now() + 8760.hours
            val response =
                post(
                    "/reconcileStock",
                    ReconcileStockRequest(
                        product.sku,
                        23,
                        reconciliationDate
                            .toEpochMilliseconds(),
                    ),
                )
            response.assertFailedWith(
                HttpStatusCode.BadRequest,
                jsonObject(
                    "errors" to
                        jsonArray(
                            jsonObject(
                                "reconciliationDate" to
                                    "cannot-be-in-the-future"
                                        .json
                            )
                        )
                ),
                emptySet(),
            )
        }
    }

    @Test
    @DisplayName(
        "fail for reconciliation dates <= than the last reconciliation date"
    )
    fun failForReconciliationDatesEarlierThanLast() {
        val products = setOf(product)
        val initialProductStock =
            InventoryLine(
                    product.id,
                    50,
                    Clock.System.now(),
                )
                .getOrFail()
        val initialInventory = setOf(initialProductStock)
        testInventoryApi(products, initialInventory) {
            val reconciliationDate =
                initialProductStock.reconciliationDate -
                    3.hours
            val response =
                post(
                    "/reconcileStock",
                    ReconcileStockRequest(
                        product.sku,
                        23,
                        reconciliationDate
                            .toEpochMilliseconds(),
                    ),
                )
            response.assertFailedWith(
                HttpStatusCode.BadRequest,
                jsonObject(
                    "errors" to
                        jsonArray(
                            jsonObject(
                                "reconciliationDate" to
                                    "cannot-be-earlier-than-last"
                                        .json
                            )
                        )
                ),
                initialInventory,
            )
        }
    }

    @Test
    @DisplayName("fail for negative amounts")
    fun failForNegativeAmounts() {
        val products = setOf(product)
        testInventoryApi(products) {
            val response =
                post(
                    "/reconcileStock",
                    ReconcileStockRequest(product.sku, -2),
                )
            response.assertFailedWith(
                HttpStatusCode.BadRequest,
                jsonObject(
                    "errors" to
                        jsonArray(
                            jsonObject(
                                "amount" to
                                    "must-be-non-negative"
                                        .json
                            )
                        )
                ),
                emptySet(),
            )
        }
    }

    @Test
    @DisplayName(
        "return 5XX when there is an unexpected (e.g. infrastructure) error"
    )
    fun failForUnexpectedErrors() {
        val failingProductsRepository =
            object : ProductsRepository {
                override fun getProduct(
                    sku: String
                ): Product? =
                    throw RuntimeException(
                        "Database is unreachable!"
                    )
            }
        val inventoryApiServer =
            InventoryApiServer(
                InventoryService(
                    Clock.System,
                    failingProductsRepository,
                    InMemoryInventoryRepository(emptySet()),
                )
            )
        testApplication {
            val httpClient = createClient {
                install(ContentNegotiation) { json() }
            }
            application {
                with(inventoryApiServer) { module() }
            }
            val response =
                httpClient.post("/reconcileStock") {
                    contentType(
                        ContentType.Application.Json
                    )
                    setBody(
                        ReconcileStockRequest("foo", 23)
                    )
                }
            assertEquals(
                HttpStatusCode.InternalServerError,
                response.status,
            )
        }
    }
}

fun <E, A> Either<E, A>.getOrFail(): A =
    this.fold(
        { fail("Right expected but got $it") },
        { it },
    )
