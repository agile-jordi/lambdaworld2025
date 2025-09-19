package com.agilogy.lambdaworld2025.inventory.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.*
import kotlin.time.Clock

class InventoryApiServerTest : FunSpec() {
    init {
        val fixedInstant = Clock.System.now()
        val testClock =
            object : Clock {
                override fun now() = fixedInstant
            }
        val inventoryApiServer = InventoryApiServer(clock = testClock)

        test("reconcileStock endpoint should handle json requests") {
            testApplication {
                val client = createClient { install(ContentNegotiation) { json() } }
                application { with(inventoryApiServer) { module() } }
                val request = ReconcileStockRequest("abc-123-1", 23, null)
                val response =
                    client.post("/reconcileStock") {
                        contentType(ContentType.Application.Json)
                        setBody(request)
                    }
                response.status shouldBe HttpStatusCode.OK
                response.body<ReconcileStockResponse>() shouldBe
                    ReconcileStockResponse(
                        request.sku,
                        request.stock,
                        request.reconciliationDate ?: testClock.now().toEpochMilliseconds(),
                    )
            }
        }
    }
}
